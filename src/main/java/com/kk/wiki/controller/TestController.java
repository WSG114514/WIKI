package com.kk.wiki.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Value("${test.hello}")
    private String hol;

    @RequestMapping("/hello")
    public String hello() {
        return hol + " World";
    }
}
