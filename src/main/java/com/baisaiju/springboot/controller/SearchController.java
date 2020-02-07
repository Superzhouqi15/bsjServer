package com.baisaiju.springboot.controller;

import com.baisaiju.springboot.dao.SearchTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * @author dav1d
 */
@Controller
public class SearchController {
    @Autowired
    SearchTemplate searchTemplate;

    @ResponseBody
    @PostMapping("/addSearch")
    public String addSearch(Map<String, Object> data){
        searchTemplate.addSearch(data);
        return "Success";
    }
}
