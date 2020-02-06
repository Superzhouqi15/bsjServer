package com.baisaiju.springboot.controller;

import com.baisaiju.springboot.dao.ClassifierTemplate;
import com.baisaiju.springboot.dao.CompetitionTemplate;
import com.baisaiju.springboot.dao.SearchTemplate;
import com.baisaiju.springboot.dao.UserTemplate;
import com.baisaiju.springboot.entities.Competition;
import com.baisaiju.springboot.entities.Search;
import com.baisaiju.springboot.entities.User;
import com.baisaiju.springboot.utils.SortList;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;



@Controller
public class CompetitionController {
    @Autowired
    private CompetitionTemplate competitionTemplate;
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
        基于搜索记录来推荐
         */

        //搜索list
        // List<String> searchList = searchTemplate.findByOpenId(openId).getSearchHistory();



        return recommendSet;

    }

    @ResponseBody
    @PostMapping("/addCompetition")
    public String addCompetition(@RequestBody Map<String, Object> data) {
        competitionTemplate.addCompetition(data);
        classifierTemplate.addCompetition((List)data.get("type"), new ObjectId(data.get("objectId").toString()));
        return "Success";
    }
}
