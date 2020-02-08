package com.baisaiju.springboot.controller;

import com.baisaiju.springboot.dao.SearchTemplate;
import org.apache.catalina.LifecycleState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
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
    public String addSearch(@RequestBody Map<String, Object> data){
        System.out.println("搜索添加" + data.get("type"));
        List<String> list = (List<String>)(data.get("type"));
        if(list.size() == 0){
            return "empty";
        }
        searchTemplate.addSearch(data);
        return "Success";
    }
}
