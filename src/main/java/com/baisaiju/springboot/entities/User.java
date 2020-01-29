package com.baisaiju.springboot.entities;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user")
public class User {
    private String username;


    public User(){
    }

}
