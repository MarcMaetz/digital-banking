package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HomeController {
    
    @GetMapping("/")
    public Map<String, Object> home() {
        return Map.of(
            "message", "Welcome to Digital Banking API!",
            "version", "1.0.0",
            "description", "A Spring Boot microservice for digital banking operations",
            "endpoints", Map.of(
                "accounts", "/api/accounts",
                "transactions", "/api/transactions",
                "h2-console", "/h2-console"
            ),
            "features", java.util.List.of(
                "Account Management",
                "Transaction Processing", 
                "Balance Inquiries",
                "RESTful APIs",
                "H2 Database Console"
            )
        );
    }
}
