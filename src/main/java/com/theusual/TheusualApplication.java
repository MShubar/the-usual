package com.theusual;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class TheusualApplication {

    public static void main(String[] args) {
        try {
            System.out.println("Starting The Usual Application...");
            System.out.println("PORT: " + System.getenv("PORT"));
            System.out.println("DATABASE_URL: " + (System.getenv("DATABASE_URL") != null ? "SET" : "NOT SET"));
            System.out.println("Java Version: " + System.getProperty("java.version"));
            
            ConfigurableApplicationContext context = SpringApplication.run(TheusualApplication.class, args);
            System.out.println("Application started successfully on port: " + context.getEnvironment().getProperty("server.port"));
        } catch (Exception e) {
            System.err.println("Failed to start application: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
