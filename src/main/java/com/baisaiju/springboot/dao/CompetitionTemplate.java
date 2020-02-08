package com.baisaiju.springboot.dao;

import com.baisaiju.springboot.entities.Competition;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class CompetitionTemplate {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private ClassifierTemplate classifierTemplate;

    public void save(Competition competition) {
        mongoTemplate.save(competition);
    }

    public List<Competition> findAll() {
        return mongoTemplate.findAll(Competition.class);
    }

    public Competition findOneByName(String competitionName) {
        Query query = new Query(Criteria.where("competitionName").is(competitionName));
        return mongoTemplate.findOne(query, Competition.class);
    }

    public Competition findOneByNameAndIntroduction(String competitionName, String introduction) {
        Query query = new Query(Criteria.where("competitionName").is(competitionName).and("introduction").is(introduction));
        return mongoTemplate.findOne(query, Competition.class);
    }

    public List<Competition> findFavorite(List<ObjectId> idList) {
        Query query = new Query(Criteria.where("_id").in(idList));
        return mongoTemplate.find(query, Competition.class);
    }

    public void addCompetition(Map<String, Object> data) {
        Competition competition = new Competition();
        competition.setCompetitionName(data.get("competitionName").toString());
        competition.setIntroduction(data.get("introduction").toString());
        competition.setMember(data.get("member").toString());
        competition.setMethod(data.get("method").toString());
        competition.setOrganization(data.get("organization").toString());
        competition.setType((List) data.get("type"));
        competition.setStartTime(data.get("startTime").toString());
        competition.setEndTime(data.get("endTime").toString());
        competition.setImagePathList(new ArrayList<>());
        competition.setFilePath("");
        mongoTemplate.save(competition);
        Query query = Query.query(Criteria.where("competitionName").is(data.get("competitionName").toString())
                .and("introduction").is(data.get("introduction").toString()));
        competition = mongoTemplate.findOne(query, Competition.class);
        classifierTemplate.addCompetition((List) data.get("type"), competition.getId());
    }
}
