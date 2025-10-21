package com.theusual.controllers;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "https://witty-grass-0ce71830f.1.azurestaticapps.net")
public class TestController {
    
    @GetMapping("/test-cors")
    public Map<String, String> testCors() {
        return Map.of("message", "CORS is working", "status", "success");
    }
}
