package com.baisaiju.springboot.controller;

<<<<<<< HEAD
import com.baisaiju.springboot.dao.ClassifierTemplate;
import com.baisaiju.springboot.dao.CompetitionTemplate;
import com.baisaiju.springboot.dao.SearchTemplate;
import com.baisaiju.springboot.dao.UserTemplate;
import com.baisaiju.springboot.entities.Competition;
import com.baisaiju.springboot.entities.Search;
import com.baisaiju.springboot.entities.User;
import com.baisaiju.springboot.utils.SortList;

import jdk.nashorn.internal.runtime.regexp.joni.SearchAlgorithm;

import org.bson.types.ObjectId;
=======
import com.baisaiju.springboot.dao.CompetitionTemplate;
import com.baisaiju.springboot.entities.Competition;
import com.baisaiju.springboot.utils.SortList;
>>>>>>> 6ff4a82f72e7f324976de3ecd6ec66662d253660
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

<<<<<<< HEAD

=======
import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.toMap;
>>>>>>> 6ff4a82f72e7f324976de3ecd6ec66662d253660

@Controller
public class CompetitionController {
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
}
