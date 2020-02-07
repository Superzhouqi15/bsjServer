package com.baisaiju.springboot.entities;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

import java.util.List;
import java.util.Stack;

/**
 * @author dav1d
 */
@Data
@Document(collection = "search")
public class Search {
    private ObjectId id;
    private String openId;
    private Stack<List<String>> typeStack;

}
