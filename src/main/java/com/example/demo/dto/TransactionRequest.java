package com.example.demo.dto;

import com.example.demo.entity.Transaction;
import jakarta.validation.constraints.AssertTrue;
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
    private Transaction.TransactionType type;
    
    private String description;
    
    @AssertTrue(message = "fromAccountNumber is required for WITHDRAWAL, TRANSFER, PAYMENT, and REFUND transactions")
    public boolean isFromAccountNumberValid() {
        if (type == null) {
            return true; // Let @NotNull handle type validation
        }
        return switch (type) {
            case DEPOSIT -> true; // Not required for DEPOSIT
            case WITHDRAWAL, TRANSFER, PAYMENT, REFUND -> fromAccountNumber != null && !fromAccountNumber.isBlank();
        };
    }
    
    @AssertTrue(message = "toAccountNumber is required for DEPOSIT, TRANSFER, PAYMENT, and REFUND transactions")
    public boolean isToAccountNumberValid() {
        if (type == null) {
            return true; // Let @NotNull handle type validation
        }
        return switch (type) {
            case WITHDRAWAL -> true; // Not required for WITHDRAWAL
            case DEPOSIT, TRANSFER, PAYMENT, REFUND -> toAccountNumber != null && !toAccountNumber.isBlank();
        };
    }
}
