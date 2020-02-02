package com.baisaiju.springboot.controller;

import com.baisaiju.springboot.dao.CompetitionTemplate;
import com.baisaiju.springboot.entities.Competition;
import com.baisaiju.springboot.utils.SortList;
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

        return "recommend";
    }
}
