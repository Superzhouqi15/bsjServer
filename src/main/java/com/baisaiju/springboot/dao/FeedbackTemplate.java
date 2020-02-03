package com.baisaiju.springboot.dao;

import com.baisaiju.springboot.entities.Feedback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class FeedbackTemplate {
    @Autowired
    MongoTemplate mongoTemplate;

    public void save(Feedback feedback){
        mongoTemplate.save(feedback);
    }
}
