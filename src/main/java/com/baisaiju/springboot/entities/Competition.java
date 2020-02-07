package com.baisaiju.springboot.entities;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

import java.util.List;

@Data
@Document(collection = "competition")
public class Competition {
    private ObjectId id;
    private String organization;
    private String member;
    private String method;
    private String startTime;
    private String endTime;
    private String introduction;
    private String competitionName;
    private double star;
    private List<String> type;
}
