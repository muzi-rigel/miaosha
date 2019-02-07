package com.miaoshaproject.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestApplication {

    @RequestMapping("/show")
    public String show(){
        return "show";
    }

}

