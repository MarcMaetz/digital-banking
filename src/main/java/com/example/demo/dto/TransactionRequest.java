package com.example.demo.dto;

import com.example.demo.entity.Transaction;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionRequest {
    
    @NotBlank
    private String fromAccountNumber;
    
    @NotBlank
    private String toAccountNumber;
    
    @NotNull
    @Positive
    private BigDecimal amount;
    
    @NotNull
    private Transaction.TransactionType type;
    
    private String description;
}
