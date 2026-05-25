package com.team5.web_ide.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/api/v1/hello")
    public String helloSwagger() {
        return "web-ide backend connected";
    }

    @GetMapping("/api/v1/health")
    public Map<String, String> health() {
        return Map.of(
                "service", "web-ide",
                "status", "ok"
        );
    }
}
