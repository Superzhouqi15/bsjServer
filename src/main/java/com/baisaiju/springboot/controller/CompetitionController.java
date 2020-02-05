package com.baisaiju.springboot.controller;

import com.baisaiju.springboot.dao.CompetitionTemplate;

import com.baisaiju.springboot.entities.Competition;
import com.baisaiju.springboot.utils.SortList;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.PrivateKeyResolver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import javax.annotation.processing.Completion;

import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.toMap;

import java.net.DatagramSocket;

@Controller
public class CompetitionController {
	@Autowired
	private CompetitionTemplate competitionTemplate;

	static int recommendNum = 5;
	static int userNum = 10; // number of users
	static int compNum = 10; // number of competitions
	private static int[][] dataset;// dataset of user-competition-like
	private static double[][] ds_interest; // dataset of user-comp-interest
	private static int[][] recommend; // recommend every user N competitions
	private sim[][] itemSim; // matrix of similarity 已排序
	private double[][] itemSim_un; // 未排序

	public static class sim {
		double value;
		int num; // No. of the competition
	}

	@ResponseBody
	@GetMapping("/addCompetition")
	public void addCompetition() {
//        String competitionName = (String)map.get("competitionName");
//        List<String> type = (List<String>)map.get("type");

		String competitionName = "蓝桥杯";
		List<String> type = new ArrayList<String>();
		type.add("计算机");
		type.add("算法");
//        type.add("建模");

		Competition competition = new Competition();
		competition.setCompetitionName(competitionName);
		competition.setType(type);

		System.out.println("保存");
		competitionTemplate.save(competition);

	}

	@ResponseBody
	@GetMapping("/findAll")
	public List<Competition> findAll() {
		return competitionTemplate.findAll();
	}

	// 计算向量A与B的相似性
	public void similarity(int[] itemA, int[] itemB) {

		int markUser = 0; // 比赛A与B都被用户标记的用户个数
		double simi = 0.0;
		int countA = 0;
		int countB = 0;

		int i;
		for (i = 0; i < usersum; i++) {
			if (itemA[i] > 0 && itemB[i] > 0) {
				markUser++;// 查找comp A与comp B的都被用户标记的用户个数
			}
			if (itemA[i] > 0) {
				countA++;// 评论comp A的用户数量
			}
			if (itemB[i] > 0) {
				countB++;// 标记comp B的用户数量
			}
		}
		double temp = Math.sqrt(countA * countB);
		if (temp == 0)
			return 0;
		else {
			simi = markUser / temp;
			return simi;
		}
	}

	// 相似度矩阵排序（由高到低）
	public static void quickSort(int x, int start, int end) {
		if (start < end) {

			double base = itemSim[x][start].value; // 选定的基准值（第一个数值作为基准值）
			double temp; // 记录临时中间值
			int i_tmp;
			int i = start, j = end;
			do {
				while ((itemSim[x][i].value > base) && (i < end))
					i++;
				while ((itemSim[x][j].value < base) && (j > start))
					j--;
				if (i <= j) {
					temp = itemSim[x][i].value;
					itemSim[x][i].value = itemSim[x][j].value;
					itemSim[x][j].value = temp;
					i_tmp = itemSim[x][i].num;
					itemSim[x][i].num = itemSim[x][j].num;
					itemSim[x][j].num = i_tmp;
					i++;
					j--;
				}
			} while (i <= j);
			if (start < j)
				quickSort(x, start, j);
			if (end > i)
				quickSort(x, i, end);
		}
	}

	public static int sort() {
		for (int i = 0; i < itemsum; i++) {
			for (int j = 0; j < itemsum; ++j) {
				itemSim[i][j].num = j;
				itemSim[i][j].value = itemSim_un[i][j];
			}
			quickSort(i, 0, itemsum - 1);
		}
		return 1;
	}

	// 得到用户i对比赛j预测兴趣程度，用于推荐，k为推荐数目
	public static double getUserLikeComp(int i, int j, int k) {
		for (int x = 0; x < k; x++)// 从物品j最相似的k个物品中，找出用户i有过行为的比赛
		{
			// System.out.println(simiItem[j][x].num);
			if (dataset[itemSim[j][x].num][i] > 0)// 若这个用户同样对相似比赛也有过行为
			{
				ds_interest[i][j] += itemSim[j][x].value;
			}
		}
		return ds_interest[i][j];
	}

	// 通过比赛兴趣程度，推荐前recommendNum个
	public static int getRecommend() {
		int maxnum;// 当前最感兴趣比赛号
		for (int i = 0; i < userNum; i++) {
			int[] finflag = new int[compNum];
			for (int x = 0; x < recommendNum; x++)// 推荐recommendNum个
			{
				maxnum = 0;
				while (maxnum < compNum && finflag[maxnum] != 0)
					maxnum++;
				for (int j = 0; j < compNum; j++) // 每循环一次就寻找此次感兴趣最大的比赛
				{
					if (ds_interest[i][maxnum] < ds_interest[i][j] && finflag[j] == 0)
						maxnum = j;
				}
				finflag[maxnum] = 1;
				if (ds_interest[i][maxnum] != 0)
					recommend[i][x] = maxnum + 1;// recommend数组从1开始使用
			}
		}
		return 1;
	}

	@GetMapping("/recommend")
	public String recommend(@RequestParam String competitionName, Model model) {

		dataset = new int[userNum][compNum];
		dataset[0][0] = 0;
		ds_interest = new double[userNum][compNum];
		ds_interest[0][0] = 0.0;
		itemSim = new sim[compNum][compNum];
		itemSim_un = new double[compNum][compNum];

		// 获得初始数据集
		int i, j, k = recommendNum;
		for (i = 0; i < compNum; ++i)
			for (j = 0; j < compNum; ++j)
				itemSim[i][j] = new sim();

		// 计算相似性矩阵并排序
		for (i = 0; i < compNum; i++) {
			for (j = 0; j < compNum; j++) {
				itemSim_un[i][j] = similarity(dataset[i], dataset[j]);
				if (i == j)
					itemSim_un[i][j] = 0;
			}
		}
		sort();
		
		//得到用户对比赛感兴趣程度矩阵
		for(i=0;i<userNum;i++)
		{
			for(j=0;j<compNum;j++)
			{
				if(train[j][i]==0)//如果用户i对比赛j没有过行为，才计算i对j的预测兴趣程度
			    	getUserLikeItem(i,j,k);
			}
		}
		
		//推荐前recommendNum个比赛
		getRecommend();
		
		/*
		 * Map<Competition, Double> sortMap = new HashMap<Competition, Double>();
		 * 
		 * Competition competition = competitionTemplate.findOneByName(competitionName);
		 * List<String> type = competition.getType(); //get the type of that competition
		 * 
		 * List<Competition> allCompetiton = competitionTemplate.findAll(); //get all
		 * competitions
		 * 
		 * //to calculate the length of intersection and union set of that competition
		 * to all competitions (competition type) for (Competition c : allCompetiton) {
		 * double intersection = 0;
		 * 
		 * for (String t : type) { for (String ct : c.getType()) { if (t.equals(ct)) {
		 * intersection++; } } } double union = type.size() + c.getType().size() -
		 * intersection;
		 * 
		 * 
		 * double Jaccard = intersection / union; System.out.println("交集长度" +
		 * intersection + "并集长度" + union); sortMap.put(c, Jaccard);
		 * 
		 * }
		 */

		List<Competition> list;
		list = SortList.sortByValueDescending(sortMap);

		model.addAttribute("list", list);
		// return SortList.sortByValueDescending(sortMap);
		return "recommend";
	}

}
