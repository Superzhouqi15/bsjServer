package com.baisaiju.springboot.controller;

import com.baisaiju.springboot.dao.CompetitionTemplate;
import com.baisaiju.springboot.dao.UserTemplate;
import com.baisaiju.springboot.entities.Competition;
import com.baisaiju.springboot.entities.User;
import com.baisaiju.springboot.utils.SortList;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.toMap;

@Controller
public class CompetitionController {
    @Autowired
    private CompetitionTemplate competitionTemplate;

    @Autowired
    private UserTemplate userTemplate;


    @ResponseBody
    @GetMapping("/findAll")
    public List<Competition> findAll() {
        return competitionTemplate.findAll();
    }


    @ResponseBody
    @PostMapping("/recommend")
    public Set<Competition> recommend(Map<String, Object> map) {
        /*
        去重，3个比赛是基于内容，3个比赛是基于用户，4个比赛是基于搜索
         */
        Set<Competition> recommendSet = new HashSet<>();
        String openId = (String) map.get("openId");
        User user = userTemplate.findByOpenId(openId);
        //获取用户喜爱的标签
        List<String> myType = user.getType();

        //获取用户喜爱的比赛
        List<Competition> myCompetition = competitionTemplate.findFavorite(user.getFavorite());

        Map<String, Integer> typeMap = new HashMap<>(); //最后的标签合集
        for(String type : myType){
            typeMap.put(type, 3);
        }

        for(Competition competition : myCompetition){
            for(String type : competition.getType()){
                int count = typeMap.containsKey(type)?typeMap.get(type):0;
                typeMap.put(type, count + 1);
            }
        }

        List<String> finalTypeList = SortList.sortByValueDescending(typeMap);
        myType.clear(); //复用这个list，防止调用内存太多

        for(int i = 0;i < 3;i++){  //选出最多3个标签
            String type = finalTypeList.get(i);
            if(type != null){
                myType.add(type);
            }else{
                break;
            }
        }

        Map<Competition, Double> competitionDoubleMap = new HashMap<Competition, Double>();
        List<Competition> allCompetiton = competitionTemplate.findAll();
        for (Competition c : allCompetiton) {
            double jiao = 0;
            for (String t : myType) {
                for (String ct : c.getType()) {
                    if (t.equals(ct)) {
                        jiao++;
                    }
                }
            }
            double bin = myType.size() + c.getType().size() - jiao;
            double rate = jiao / bin;
            competitionDoubleMap.put(c, rate);
        }


        List<Competition> basedOnContentRecommendlist = SortList.sortByValueDescending(competitionDoubleMap);

        //选出基于内容的最多3个比赛来推荐，这里是担心比赛不够多
        int i = 0;
        while(i < 3 && basedOnContentRecommendlist.get(i) != null){
            recommendSet.add(basedOnContentRecommendlist.get(i++));
        }





        /*
        基于用户的相似度
         */
        List<User> allUser = userTemplate.findAll();
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

        return recommendSet;
    }

    @ResponseBody
    @PostMapping("/addCompetition")
    public String addCompetition(@RequestBody Map<String, Object> data){
       Competition competition = new Competition();
       competition.setCompetitionName(data.get("competitionName").toString());
       competition.setIntroduction(data.get("introduction").toString());
       competition.setMember(data.get("member").toString());
       competition.setMethod(data.get("method").toString());
       competition.setOrganization(data.get("organization").toString());
       competition.setType((List) data.get("type"));
       competitionTemplate.save(competition);
       return "Success";
    }
}
