package com.baisaiju.springboot.entities;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dav1d
 */
@Document(collection = "search")
public class Search {
    private ObjectId id;
    private String openId;
    private List<String> searchHistory;

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public List<String> getSearchHistory() {
        return searchHistory;
    }

    public void setSearchHistory(List<String> searchHistory) {
        this.searchHistory = searchHistory;
    }

    public void addSearchHistory(String history){
        List<String> temp = this.getSearchHistory();
        if (temp == null){
            temp = new ArrayList<>();
        }
        temp.add(history);
    }
}
