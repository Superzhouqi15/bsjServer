package com.baisaiju.springboot.controller;

import com.baisaiju.springboot.dao.FeedbackTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * @author dav1d
 */
@Controller
public class FeedbackController {
    @Autowired
    FeedbackTemplate feedbackTemplate;

    @ResponseBody
    @PostMapping("/feedback")
    public String addFeedback(@RequestBody Map<String, Object> data){
        feedbackTemplate.add(data);
        return "Success";
    }
}
