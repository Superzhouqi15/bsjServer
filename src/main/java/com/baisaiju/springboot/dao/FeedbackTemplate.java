package com.baisaiju.springboot.dao;

import com.baisaiju.springboot.entities.Feedback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class FeedbackTemplate {
    @Autowired
    MongoTemplate mongoTemplate;

    public void save(Feedback feedback){
        mongoTemplate.save(feedback);
    }

    public void add(Map<String, Object>data){
        Feedback feedback = new Feedback();
        feedback.setContent(data.get("content").toString());
        feedback.setOpenId(data.get("openId").toString());
        feedback.setEmail(data.get("email").toString());
        mongoTemplate.save(feedback);
    }
}
