package com.example.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Digital Banking API")
                        .description("A comprehensive Spring Boot microservice for digital banking operations. " +
                                   "This API demonstrates modern banking features including account management, " +
                                   "transaction processing, and balance inquiries.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Banking API Team")
                                .email("api@banking.com")
                                .url("https://banking-api.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Development Server"),
                        new Server()
                                .url("https://api.banking.com")
                                .description("Production Server")
                ));
    }
}
