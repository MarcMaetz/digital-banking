package com.example.demo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionRequest {
    
    private String fromAccountNumber;
    
    private String toAccountNumber;
    
    @NotNull
    @Positive
    private BigDecimal amount;
    
    @NotNull
    private TransactionType type;
    
    private String description;

}
