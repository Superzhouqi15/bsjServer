package com.baisaiju.springboot.controller;

import com.baisaiju.springboot.dao.ClassifierTemplate;
import com.baisaiju.springboot.dao.CompetitionTemplate;
import com.baisaiju.springboot.entities.Competition;
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
    private ClassifierTemplate classifierTemplate;

    @ResponseBody
    @GetMapping("/addCompetition1")
    public void addCompetition1() {
//        String competitionName = (String)map.get("competitionName");
//        List<String> type = (List<String>)map.get("type");

        final String competitionName = "蓝桥杯";
        final List<String> type = new ArrayList<String>();
        type.add("计算机");
        type.add("算法");
        // type.add("建模");

        final Competition competition = new Competition();
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

    @GetMapping("/recommend")
    public String recommend(@RequestParam final String competitionName, final Model model) {

        final Map<Competition, Double> sortMap = new HashMap<Competition, Double>();

        final Competition competition = competitionTemplate.findOneByName(competitionName);
        final List<String> type = competition.getType();

        final List<Competition> allCompetiton = competitionTemplate.findAll();

        for (final Competition c : allCompetiton) {
            double jiao = 0;

            for (final String t : type) {
                for (final String ct : c.getType()) {
                    if (t.equals(ct)) {
                        jiao++;
                    }
                }
            }
            final double bin = type.size() + c.getType().size() - jiao;

            final double rate = jiao / bin;
            System.out.println("交集长度" + jiao + "并集长度" + bin);
            sortMap.put(c, rate);

        }
        List<Competition> list;
        list = SortList.sortByValueDescending(sortMap);
        model.addAttribute("list", list);
        // return SortList.sortByValueDescending(sortMap);
        return "recommend";
    }

    @ResponseBody
    @PostMapping("/addCompetition")
    public String addCompetition(@RequestBody Map<String, Object> data) {
        competitionTemplate.addCompetition(data);
        classifierTemplate.addCompetition((List)data.get("type"), new ObjectId(data.get("objectId").toString()));
        return "Success";
    }
}
