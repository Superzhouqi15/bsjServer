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


        Map<Competition, Double> competitionMap = new HashMap<Competition, Double>();

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
            competitionMap.put(c, rate);

        }
        List<Competition> recommendlist;
        recommendlist = SortList.sortByValueDescending(competitionMap);

        Set<Competition> recommendSet = new HashSet<>();  //去重
        for(int i = 0;i < 4;i++){  //选出基于内容的最多4个比赛来推荐，这里是担心比赛不够多
            Competition competition = recommendlist.get(i);
            if(competition != null){
                recommendSet.add(competition);
            }else{
                break;
            }
        }

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
