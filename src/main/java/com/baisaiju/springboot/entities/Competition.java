package com.baisaiju.springboot.entities;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

import java.util.List;
import java.util.Objects;

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
    private List<String> imagePathList;
    private String filePath;
    private List<String> type;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Competition)) return false;
        Competition that = (Competition) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
