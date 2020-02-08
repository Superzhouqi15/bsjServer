package com.baisaiju.springboot.entities;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

import java.util.List;

/**
 * @author dav1d
 */
@Data
@Document(collection = "search")
public class Search {
    private ObjectId id;
    private String openId;
    private int current;
    private List<String>[] searchHistory;
    
	public void addSaerch(List<String> res) {
        this.getSearchHistory()[this.getCurrent()] = res;
        this.setCurrent((this.getCurrent() + 1) % 10);
	}


}
