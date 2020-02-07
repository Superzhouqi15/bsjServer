package com.baisaiju.springboot.controller;

<<<<<<< HEAD
import com.baisaiju.springboot.dao.ClassifierTemplate;
import com.baisaiju.springboot.dao.CompetitionTemplate;
<<<<<<< HEAD
import com.baisaiju.springboot.dao.SearchTemplate;
import com.baisaiju.springboot.dao.UserTemplate;
=======

>>>>>>> dc157a4d3e4caf4fccd30e374811ec2703681375
import com.baisaiju.springboot.entities.Competition;
import com.baisaiju.springboot.entities.Search;
import com.baisaiju.springboot.entities.User;
import com.baisaiju.springboot.utils.SortList;
<<<<<<< HEAD

import jdk.nashorn.internal.runtime.regexp.joni.SearchAlgorithm;

import org.bson.types.ObjectId;
=======
import com.baisaiju.springboot.dao.CompetitionTemplate;
import com.baisaiju.springboot.entities.Competition;
import com.baisaiju.springboot.utils.SortList;
>>>>>>> 6ff4a82f72e7f324976de3ecd6ec66662d253660
=======
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.PrivateKeyResolver;

>>>>>>> dc157a4d3e4caf4fccd30e374811ec2703681375
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

<<<<<<< HEAD
<<<<<<< HEAD

=======
=======
import javax.annotation.processing.Completion;

>>>>>>> dc157a4d3e4caf4fccd30e374811ec2703681375
import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.toMap;
>>>>>>> 6ff4a82f72e7f324976de3ecd6ec66662d253660

import java.net.DatagramSocket;

@Controller
public class CompetitionController {
<<<<<<< HEAD
    @Autowired
    private CompetitionTemplate competitionTemplate;
<<<<<<< HEAD
    @Autowired
    private ClassifierTemplate classifierTemplate;
    @Autowired
    private UserTemplate userTemplate;
    @Autowired
    private SearchTemplate searchTemplate;

    @ResponseBody
    @GetMapping("/findAll")
    public List<Competition> findAll() {
        return competitionTemplate.findAll();
    }



    //@ResponseBody
    @GetMapping("/recommend")
    public Set<Competition> recommend(Map<String, Object> map) {

        /*
        现在有4种方法，
            基于初始标签，
            基于收藏比赛的相似度，
            基于用户的相似度，
            基于搜索记录
        去重，
             3个比赛是基于初始标签，
             2个比赛是基于收藏比赛的相似度，
             2个比赛是基于用户的相似度，
             3个比赛是基于搜索记录
         */
        Set<Competition> recommendSet = new HashSet<>();
        String openId = (String) map.get("openId");
        User user = userTemplate.findByOpenId(openId);
        //获取用户喜爱的标签
        List<String> myType = user.getType();



        /*
        第一部分：基于初始标签
         */
        Map<Competition, Double> competitionDoubleMap = new HashMap<Competition, Double>();
        //获取所有比赛
        List<Competition> allCompetition = competitionTemplate.findAll();
        for (Competition c : allCompetition) {
            double jiao = 0;
            for (String t : myType) {
                for (String ct : c.getType()) {
                    if (t.equals(ct)) {
                        jiao++;
                    }
                }
            }
            final double bin = myType.size() + c.getType().size() - jiao;
            final double rate = jiao / bin;
            System.out.println("交集长度" + jiao + "并集长度" + bin);
            competitionDoubleMap.put(c, rate);
        }



        List<Competition> basedOnTypeRecommendlist = SortList.sortByValueDescending(competitionDoubleMap);

        //选出基于初始标签的最多3个比赛来推荐，这里是担心比赛不够多
        int i = 0;
        while(i < 3 && basedOnTypeRecommendlist.get(i) != null){
            recommendSet.add(basedOnTypeRecommendlist.get(i++));
        }

        /*
        第二部分：基于收藏比赛的相似度
         */
        //获取用户喜爱的比赛
        List<Competition> myCompetition = competitionTemplate.findFavorite(user.getFavorite());
        //获取所有用户
        List<User> allUser = userTemplate.findAll();

        //得到用户、比赛的二维数组
        int allUserSize = allUser.size();
        int allCompetitionSize = allCompetition.size();

        //用户和比赛的收藏与否的01数组
        int[][] UCArr = new int[allUserSize][allCompetitionSize];
        int i1 = 0;
        for(User nowUser : allUser){
            int j1 = 0;
            List<Competition> nowUserCompetition = competitionTemplate.findFavorite(nowUser.getFavorite());
            for(Competition competition : allCompetition){
                if(nowUserCompetition.contains(competition)){
                    UCArr[i1][j1] = 1;
                }else{
                    UCArr[i1][i1] = 0;
                }
                j1++;
            }
            i1++;
        }

        //同时收藏比赛A和比赛B的用户数的数组
        //后面也许可以把这个数组改成相似度的数组
        int[][] CCArr = new int[allCompetitionSize][allCompetitionSize];
        //某比赛被收藏的用户数
        int[] CArr = new int[allCompetitionSize];
        for(int index1 = 0;index1 < allUserSize;index1++){
            for(int index2 = 0;index2 < allCompetitionSize;index2++){
                if(UCArr[index1][index2] == 1){
                    CArr[index2]++;
                    for(int index3 = index2 + 1;index3 < allCompetitionSize;index3++){
                        if(UCArr[index1][index2] == UCArr[index1][index3] ){ //同时参加比赛A与比赛B的用户数
                            CCArr[index2][index3]++;
                            CCArr[index3][index2]++;
                        }
                    }
                }

            }
        }

        //计算两比赛相似度的数组
        double[][] semiArr = new double[allCompetitionSize][allCompetitionSize];
        for(int index1 = 0;index1 < allCompetitionSize;index1++){
            for(int index2 = 0;index2 < allCompetitionSize;index2++){
                double num = Math.sqrt(CArr[index1] * CArr[index2]);
                if(num == 0){
                    semiArr[index1][index2] = 0;
                }
                semiArr[index1][index2] = CCArr[index1][index2] / num;
            }
        }

        //找到用户在全用户的位置
        int location = allUser.indexOf(user);
        int myCompetitionSize = myCompetition.size();

        //用户收藏的比赛在全部比赛中的下标数组
        int[] userCompetitionIndex = new int[myCompetitionSize];

        //用户未收藏的比赛在全部比赛中的下标数组
        int[] notUserCompetitionIndex = new int[allCompetitionSize - myCompetitionSize];

        int ii = 0;
        int jj = 0;
        for(int index1 = 0;index1 < allCompetitionSize;index1++){
            if(UCArr[location][index1] == 1){ //这是用户收藏的比赛, index是用户收藏的比赛的下标
                userCompetitionIndex[ii++] = index1;
            }else{                            //这是用户未收藏的比赛, index是用户收藏的比赛的下标
                notUserCompetitionIndex[jj++] = index1;
            }
        }
        //生成一个比赛和相似度的map
        Map<Competition, Double> competitionDoubleMap1 = new HashMap<>();


        for(int index1 = 0;index1 < allCompetitionSize - myCompetitionSize;index1++){
            int index2 = notUserCompetitionIndex[index1]; //获取不在收藏列表的比赛下标
            double sum = 0;
            int count = 0; //相似度不为0的就让count+1
            for(int index3 = 0;index3 < myCompetitionSize;index3++){
                int index4 = userCompetitionIndex[index3]; //获取在收藏列表的比赛下标
                if(semiArr[index2][index4] != 0){
                    sum += semiArr[index2][index4];
                    count++;
                }
            }
            if(count == 0){
                competitionDoubleMap1.put(allCompetition.get(index2),0.0);
            }else{
                competitionDoubleMap1.put(allCompetition.get(index2),sum / count);
            }
        }
        //对map进行排序
        List<Competition> basedOnContentRecommendlist = SortList.sortByValueDescending(competitionDoubleMap1);
        //选出基于内容的最多3个比赛来推荐，这里是担心比赛不够多
        i = 0;
        while(i < 2 && basedOnContentRecommendlist.get(i) != null){
            recommendSet.add(basedOnContentRecommendlist.get(i++));
        }





        /*
        第三部分：基于用户的相似度
         */
        Map<User, Double> userDoubleMap = new HashMap<>();
        for(User otherUser : allUser){
            double jiao = 0;
            for(Competition mCompetition : myCompetition){
                List<Competition> otherUserCompetition = competitionTemplate.findFavorite(otherUser.getFavorite());
                for (Competition competition : otherUserCompetition){
                    if(competition.equals(mCompetition)){
                        jiao++;
                    }
                }
            }
            double bin = otherUser.getFavorite().size() + myCompetition.size() - jiao;
            double rate = jiao / bin;
            userDoubleMap.put(otherUser, rate);
        }


        //基于用户相似度推荐
        List<User> basedOnUserRecommendList = SortList.sortByValueDescending(userDoubleMap);

        int j = 0;
        int index = 0;
        while(j < 3 && basedOnContentRecommendlist.get(index) != null){
            User recommendUser = basedOnUserRecommendList.get(index++);
            if(!recommendUser.equals(user)){//相似度最高的用户应该就是自己本身，先排除本身
                List<Competition> recommendUserCompetition = competitionTemplate.findFavorite(recommendUser.getFavorite());
                for(Competition competition : recommendUserCompetition){
                    if(!recommendSet.contains(competition)){
                        recommendSet.add(competition);
                        j++;
                    }
                }
            }
        }


        /*
         * 基于搜索记录来推荐
         * 目的在于将推荐内容向外扩展，不仅局限于用户关注的标签
         */

        
        
        //获得所有搜索标签
        List<Search> allSearch = searchTemplate.findAll();
             
        //得到搜索记录-用户二维数组  0代表未搜索过该标签  1表示搜索过该标签
        int allSearchSize = allSearch.size();
        int [][]userSearch = new int [allSearchSize][allUserSize];
        int iii = 0;
        for(User nowUser : allUser){
            int jjj = 0;
            List<Search> nowUserSearch = searchTemplate.findByOpenId(nowUser.openId).getSearchHistory();
            for(Search search : allSearch){
                if(nowUserSearch.contains(search)){
                    userSearch[jjj][iii] = 1;
                }else{
                    userSearch[jjj][iii] = 0;
                }
                jjj++;
            }
            iii++;
        }
        
        //同时搜索该标签的用户数
        int[][] searchNum = new int[allSearchSize][allSearchSize];
        //搜索标签出现数
        int[] searchNum_ = new int[allSearchSize];
        for(int i3 = 0;i3 < allUserSize;i3++){
            for(int j3 = 0;j3 < allSearchSize;j3++){
                if(userSearch[j3][i3] == 1){
                    searchNum_[j3]++;
                    for(int k3 = j3 + 1;k3 < allSearchSize;k3++){
                        if(userSearch[j3][i3] == userSearch[k3][i3] ){ //同时搜索标签AB
                            searchNum[j3][k3]++;
                            searchNum[k3][j3]++;
                        }
                    }
                }
            }
        }

        //计算搜索标签相似度的数组
        double[][] simi = new double[allSearchSize][allSearchSize];
        for(int i3 = 0;i3 < allSearchSize;i3++){
            for(int j3 = 0;j3 < allSearchSize;j3++){
                double num = Math.sqrt(searchNum_[i3] * searchNum_[j3]);
                if(num == 0){
                    simi[i3][j3] = 0;
                }
                simi[i3][j3] = searchNum[i3][j3] / num;
            }
        }

        //我的搜索记录
        List<String> mySearchList = searchTemplate.findByOpenId(openId).getSearchHistory();
        int mySearchSize = mySearchList.size();
        
        //用户搜索记录（标签）在全部搜索标签中的下标数组
        int[] userSearchIndex = new int[mySearchSize];

        //用户未搜索过的标签在全部搜索标签中的下标数组
        int[] notUserSearchIndex = new int[allSearchSize - mySearchSize];

        ii = 0;
        jj = 0;
        for(int i3 = 0;i3 < allSearchSize;i3++){
            if(userSearch[i3][location] == 1){ //用户搜索记录标签, i3是用户过搜索记录的标签的下标
                userSearchIndex[ii++] = i3;
            }else{                             //这是用户没搜索过的标签
                notUserSearchIndex[jj++] = i3;
            }
        }
        //生成一个搜索记录标签和相似度的map
        Map<Search, Double> searchMap = new HashMap<>();


        for(int iii = 0;iii < allSearchSize - mySearchSize;iii++){
            int mark1 = notUserSearchIndex[iii]; //获取未搜索过的标签下标
            double sum = 0;
            int count = 0; //相似度不为0的就让count+1
            for(int jjj = 0;jjj < mySearchSize;jjj++){
                int mark2 = userSearchIndex[jjj]; //获取搜索过的标签下标
                if(simi[mark1][mark2] != 0){
                    sum += simi[mark1][mark2];
                    count++;
                }
            }
            if(count == 0){
                searchMap.put(allSearch.get(mark1),0.0);
            }else{
                searchMap.put(allSearch.get(mark1),sum / count);
            }
        }
        //对map进行排序，抽出两个
        List<Search> searchRecommendList = SortList.sortByValueDescending(searchMap);
        Set<String> typeRecommendSet = new HashSet<String>();
        
        //获得两个Search对象对应的比赛标签
        for(int i = 0;i < 2;i++) {
        	List<String> tmp = searchRecommendList.get(i).geSearchHistory();
        	for(int j = 0;j<tmp.size();j++)
        		typeRecommendSet.add(tmp.get(j));
        }
        
        Map<Competition, Double> competitionDoubleMap2 = new HashMap<Competition, Double>();
        for (Competition c : allCompetition) {
            double jiao2 = 0;
            for (String t : typeRecommendSet) {
                for (String ct : c.getType()) {
                    if (t.equals(ct)) {
                        jiao2++;
                    }
                }
            }
            final double bin2 = myType.size() + c.getType().size() - jiao2;
            final double rate2 = jiao2 / bin2;
            System.out.println("交集2长度" + jiao2 + "并集2长度" + bin2);
            competitionDoubleMap2.put(c, rate2);
        }
        List<Competition> basedOnSearchRecommend = SortList.sortByValueDescending(competitionDoubleMap);

        //选出基于搜索记录的最多3个比赛来推荐
        int i = 0;
        while(i < 3 && basedOnSearchRecommend.get(i) != null){
            recommendSet.add(basedOnSearchRecommend.get(i++));
        }
        
        //List<Competition> basedOnSearchRecommend = new List<String>();   //=?
        //while(basedOnSearchRecommend)             


        return recommendSet;

    }

    @ResponseBody
    @PostMapping("/addCompetition")
    public String addCompetition(@RequestBody Map<String, Object> data) {
        competitionTemplate.addCompetition(data);
        classifierTemplate.addCompetition((List)data.get("type"), new ObjectId(data.get("objectId").toString()));
        return "Success";
=======

    @ResponseBody
    @GetMapping("/addCompetition")
    public void addCompetition(){
=======
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
>>>>>>> dc157a4d3e4caf4fccd30e374811ec2703681375
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

<<<<<<< HEAD
        Competition competition = new Competition();
        competition.setCompetitionName(competitionName);
        competition.setType(type);

        System.out.println("保存");
        competitionTemplate.save(competition);

    }

    @ResponseBody
    @GetMapping("/findAll")
    public List<Competition> findAll(){
        return competitionTemplate.findAll();
    }


    @GetMapping("/recommend")
    public String recommend(@RequestParam String competitionName, Model model){

        Map<Competition,Double> sortMap = new HashMap<Competition, Double>();

        Competition competition = competitionTemplate.findOneByName(competitionName);
        List<String> type = competition.getType();


        List<Competition> allCompetiton = competitionTemplate.findAll();

            for(Competition c:allCompetiton){
                double jiao = 0;

                for(String t:type){
                    for(String ct:c.getType()){
                        if(t.equals(ct)){
                            jiao++;
                        }
                    }
                }
                double bin = type.size() + c.getType().size() - jiao;

                double rate = jiao / bin;
                System.out.println( "交集长度" + jiao + "并集长度" + bin);
                sortMap.put(c,rate);

            }

        List<Competition> list;
        list = SortList.sortByValueDescending(sortMap);
        model.addAttribute("list", list);
        //return SortList.sortByValueDescending(sortMap);
        return "recommend";
>>>>>>> 6ff4a82f72e7f324976de3ecd6ec66662d253660
    }
=======
>>>>>>> dc157a4d3e4caf4fccd30e374811ec2703681375
}
