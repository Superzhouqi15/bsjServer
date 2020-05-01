package com.baisaiju.springboot.entities;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dav1d
 */

@Document(collection = "search")
public class Search {
    private ObjectId id;
    private String openId;
    private int current;
    private List<String>[] searchHistory;



    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public List<String>[] getSearchHistory() {
        return searchHistory;
    }

    public void setSearchHistory(List<String>[] searchHistory) {
        this.searchHistory = searchHistory;
    }

    public void addSaerch(List<String> res) {
        this.getSearchHistory()[this.getCurrent()] = res;
        this.setCurrent((this.getCurrent() + 1) % 10);
	}


}
