package com.baisaiju.springboot.entities;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author dav1d
 */
@Document(collection = "classifier")
public class Classifier{
    private ObjectId id;
    private String type;
    private List<ObjectId> competitionList;

    public void addCompetition(ObjectId objectId){
        if (this.competitionList == null){
            this.competitionList = new ArrayList<>();
        }
        this.getCompetitionList().add(objectId);
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<ObjectId> getCompetitionList() {
        return competitionList;
    }

    public void setCompetitionList(List<ObjectId> competitionList) {
        this.competitionList = competitionList;
    }
}