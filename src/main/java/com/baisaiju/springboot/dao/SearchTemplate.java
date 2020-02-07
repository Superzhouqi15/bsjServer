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

    public List<Search> findAll(){
        return mongoTemplate.findAll(Search.class);
    }
    
    public Search findByOpenId(String openId) {
        Query query = Query.query(Criteria.where("openId").is(openId));
        return mongoTemplate.findOne(query, Search.class);
    }

    public void addSearch(Map<String, Object> data) {
        Search search = this.findByOpenId(data.get("openId").toString());
        Search mainSearch = this.findByOpenId("main");
        Iterator<String> temp = ((List<String>) data.get("type")).iterator();
        String string = "";
        while (temp.hasNext()) {
            string = temp.next();
            if (search.getTypeMap().get(string) != null) {
                search.getTypeMap().put(string, search.getTypeMap().get(string) + 1);
            } else {
                search.getTypeMap().put(string, 1);
            }
            if (mainSearch.getTypeMap().get(string) != null) {
                mainSearch.getTypeMap().put(string, mainSearch.getTypeMap().get(string) + 1);
            } else {
                mainSearch.getTypeMap().put(string, 1);
            }
        }
        mongoTemplate.save(search);
        mongoTemplate.save(mainSearch);
    }

    public void newUser(Map<String, Object> data) {
        Search search = new Search();
        search.setOpenId(data.get("openId").toString());
        search.setTypeMap(new HashMap<>());
        mongoTemplate.save(search);
    }

}
