package com.baisaiju.springboot.entities;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

import java.util.Map;

/**
 * @author dav1d
 */
@Data
@Document(collection = "search")
public class Search {
    private ObjectId id;
    private String openId;
    private Map<String, Integer> typeMap;

}
