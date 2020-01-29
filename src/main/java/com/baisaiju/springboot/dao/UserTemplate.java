package com.baisaiju.springboot.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserTemplate {
    @Autowired
    private MongoTemplate mongoTemplate;

}
