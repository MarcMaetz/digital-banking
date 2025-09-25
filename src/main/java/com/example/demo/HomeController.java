package com.example.demo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Tag(name = "Home", description = "Welcome and system information")
public class HomeController {
    
    @GetMapping("/")
    @Operation(summary = "Welcome message", description = "Returns a welcome message for the Digital Banking API")
    public Map<String, Object> home() {
        return Map.of(
            "message", "Welcome to Digital Banking API!",
            "version", "1.0.0",
            "description", "A Spring Boot microservice for digital banking operations",
            "endpoints", Map.of(
                "accounts", "/api/accounts",
                "transactions", "/api/transactions",
                "swagger-ui", "/swagger-ui.html",
                "h2-console", "/h2-console"
            ),
            "features", java.util.List.of(
                "Account Management",
                "Transaction Processing", 
                "Balance Inquiries",
                "RESTful APIs",
                "Swagger Documentation",
                "H2 Database Console"
            )
        );
    }
}
