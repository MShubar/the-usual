package com.theusual.controllers;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class TestController {
    
    private String clientOrigin;
    
    @GetMapping("/test-cors")
    public Map<String, String> testCors() {
        return Map.of("message", "CORS is working", "status", "success", "allowedOrigin", clientOrigin);
    }
}
