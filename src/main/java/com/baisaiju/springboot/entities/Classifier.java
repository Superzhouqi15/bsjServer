package com.baisaiju.springboot.entities;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

/**
 * @author dav1d
 */
@Data
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
}