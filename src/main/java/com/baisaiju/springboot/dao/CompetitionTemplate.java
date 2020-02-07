package com.baisaiju.springboot.dao;

import com.baisaiju.springboot.entities.Competition;
<<<<<<< HEAD
import org.bson.types.ObjectId;
=======
>>>>>>> 6ff4a82f72e7f324976de3ecd6ec66662d253660
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
<<<<<<< HEAD
=======
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
>>>>>>> 6ff4a82f72e7f324976de3ecd6ec66662d253660

import java.util.List;
import java.util.Map;

@Component
public class CompetitionTemplate {
    @Autowired
    private MongoTemplate mongoTemplate;

<<<<<<< HEAD
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

    public List<Competition> findFavorite(List<ObjectId> idList) {
        Query query = new Query(Criteria.where("_id").in(idList));
        return mongoTemplate.find(query, Competition.class);
    }

    public void addCompetition(Map<String, Object> data){
        Competition competition = new Competition();
        competition.setCompetitionName(data.get("competitionName").toString());
        competition.setIntroduction(data.get("introduction").toString());
        competition.setMember(data.get("member").toString());
        competition.setMethod(data.get("method").toString());
        competition.setOrganization(data.get("organization").toString());
        competition.setType((List) data.get("type"));

        competition.setStartTime(data.get("startTime").toString());
        competition.setEndTime(data.get("endTime").toString());

        mongoTemplate.save(competition);
=======
    public void save(Competition competition){
        mongoTemplate.save(competition);
    }

    public List<Competition> findAll(){
        return mongoTemplate.findAll(Competition.class);
    }

    public Competition findOneByName(String competitionName){
        Query query = new Query(Criteria.where("competitionName").is(competitionName));
        return (Competition) mongoTemplate.findOne(query, Competition.class);
>>>>>>> 6ff4a82f72e7f324976de3ecd6ec66662d253660
    }
}
