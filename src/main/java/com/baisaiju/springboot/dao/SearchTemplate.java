package com.baisaiju.springboot.dao;

import com.baisaiju.springboot.entities.Search;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
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
        System.out.println(data.get("openId"));
        Search search = this.findByOpenId(data.get("openId").toString());
        Search mainSearch = this.findByOpenId("main");
        search.getTypeStack().add((List<String>)data.get("type"));
        mainSearch.getTypeStack().add((List<String>)data.get("type"));
        mongoTemplate.save(search);
        mongoTemplate.save(mainSearch);
    }

    public void newUser(Map<String, Object> data) {
        Search search = new Search();
        search.setOpenId(data.get("openId").toString());
        search.setTypeStack(new Stack<>());
        mongoTemplate.save(search);
    }

}
