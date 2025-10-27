package com.theusual.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "${CLIENT_ORIGIN:http://localhost:5173}")
public class TestController {
    
    @Value("${CLIENT_ORIGIN:http://localhost:5173}")
    private String clientOrigin;
    
    @GetMapping("/test-cors")
    public Map<String, String> testCors() {
        return Map.of("message", "CORS is working", "status", "success", "allowedOrigin", clientOrigin);
    }
}
