package com.baisaiju.springboot.dao;

import com.baisaiju.springboot.entities.Search;
import com.baisaiju.springboot.entities.User;
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
public class SearchTemplate {
    @Autowired
    MongoTemplate mongoTemplate;

    public void save(Search search){
        mongoTemplate.save(search);
    }

    public Search findByOpenId(String openId){
        Query query = Query.query(Criteria.where("openId").is(openId));
        return  mongoTemplate.findOne(query, Search.class);
    }

    public void addSearch(Map<String, Object> data){
        Search search = this.findByOpenId(data.get("openId").toString());
        search.addSearchHistory(data.get("history").toString());
        mongoTemplate.save(search);
    }

    public void newUser(Map<String, Object> data) {
        Search search = new Search();
        search.setOpenId(data.get("openId").toString());
        mongoTemplate.save(search);
    }

}
