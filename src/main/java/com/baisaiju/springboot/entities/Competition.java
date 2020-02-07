package com.baisaiju.springboot.entities;

<<<<<<< HEAD
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
=======
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "competition")
public class Competition {
    private String competitionName;
    private List<String> type;

    public Competition(){

    }

    public String getCompetitionName() {
        return competitionName;
    }

    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }

    public List<String> getType() {
        return type;
    }

    public void setType(List<String> type) {
        this.type = type;
    }
>>>>>>> 6ff4a82f72e7f324976de3ecd6ec66662d253660
}
