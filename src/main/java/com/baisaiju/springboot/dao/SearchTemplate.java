package com.baisaiju.springboot.dao;

import com.baisaiju.springboot.entities.Search;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Iterator;

import java.util.ArrayList;

import java.util.List;
import java.util.Map;

@Component
public class SearchTemplate {
    @Autowired
    MongoTemplate mongoTemplate;

    public void save(Search search) {
        mongoTemplate.save(search);
    }

    public List<Search> findByOpenId(String openId) {
        Query query = Query.query(Criteria.where("openId").is(openId));
        return mongoTemplate.find(query, Search.class);
    }


    public void addSearch(Map<String, Object> data) {
        String openId = data.get("openId").toString();
        Search search = new Search();
        search.setOpenId(openId);
        search.setType((List<String>) data.get("type"));
        //search.setId();

        mongoTemplate.save(search);
    }

    public void newUser(Map<String, Object> data) {
        Search search = new Search();
        search.setOpenId(data.get("openId").toString());
        //search.setTypeMap(new HashMap<>());
        mongoTemplate.save(search);
    }

}
