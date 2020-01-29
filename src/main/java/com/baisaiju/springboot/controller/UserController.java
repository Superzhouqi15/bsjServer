package com.baisaiju.springboot.controller;

import com.baisaiju.springboot.dao.UserTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class UserController {

    @Autowired
    private UserTemplate userTemplate;


}
