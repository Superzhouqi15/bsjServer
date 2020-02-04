package com.baisaiju.springboot.dao;

import com.baisaiju.springboot.entities.Competition;
import com.baisaiju.springboot.entities.Search;
import com.baisaiju.springboot.entities.User;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class UserTemplate {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private CompetitionTemplate competitionTemplate;

    public void save(User user) {
        mongoTemplate.save(user);
    }

    public User findByOpenId(String openId) {
        Query query = new Query(Criteria.where("openId").is(openId));
        return mongoTemplate.findOne(query, User.class);
    }

    public void newUser(Map<String, Object> data) {
        User user = new User();
        user.setFavorite(new ArrayList<>());
        user.setOpenId(data.get("openId").toString());
        user.setUserName(data.get("userName").toString());
        mongoTemplate.save(user);
    }

    public void addFavorite(Map<String, Object> data) {
        User user = this.findByOpenId(data.get("openId").toString());
        user.addFavorite(new ObjectId(data.get("objectId").toString()));
        mongoTemplate.save(user);
    }

    public void delFavorite(Map<String, Object> data) {
        User user = this.findByOpenId(data.get("openId").toString());
        user.delFavorite(new ObjectId(data.get("objectId").toString()));
        mongoTemplate.save(user);
    }

    public List<Competition> getFavorite(Map<String, Object> data) {
        User user = this.findByOpenId(data.get("openId").toString());
        return competitionTemplate.findFavorite(user.getFavorite());
    }


}
