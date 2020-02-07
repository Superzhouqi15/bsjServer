package com.baisaiju.springboot.entities;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "feedback")
public class Feedback {
    private ObjectId id;
    private String content;
    private String email;
    private String openId;
}
