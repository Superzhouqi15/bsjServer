package com.baisaiju.springboot.dao;

import com.baisaiju.springboot.entities.Competition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Component
public class CompetitionTemplate {
    @Autowired
    private MongoTemplate mongoTemplate;

    public void save(Competition competition){
        mongoTemplate.save(competition);
    }

    public List<Competition> findAll(){
        return mongoTemplate.findAll(Competition.class);
    }

    public Competition findOneByName(String competitionName){
        Query query = new Query(Criteria.where("competitionName").is(competitionName));
        return (Competition) mongoTemplate.findOne(query, Competition.class);
    }
}
