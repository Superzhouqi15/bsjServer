package com.baisaiju.springboot.dao;

import com.baisaiju.springboot.entities.Search;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

@Component
public class SearchTemplate {
    @Autowired
    MongoTemplate mongoTemplate;

    public void save(Search search) {
        mongoTemplate.save(search);
    }

    public Search findByOpenId(String openId) {
        Query query = Query.query(Criteria.where("openId").is(openId));
        return mongoTemplate.findOne(query, Search.class);
    }

    public void addSearch(Map<String, Object> data) {
        Search search = this.findByOpenId(data.get("openId").toString());
        search.addSaerch((List)data.get("type"));
        mongoTemplate.save(search);
    }

    public void newUser(Map<String, Object> data) {
        Search search = new Search();
        List<String> tmp[] = new ArrayList[10];
        for(int i = 0; i < 10; i++){
            tmp[i] = new ArrayList<>();
        }
        search.setOpenId(data.get("openId").toString());
        search.setSearchHistory(tmp);
        search.setCurrent(0);
        mongoTemplate.save(search);
    }

}
