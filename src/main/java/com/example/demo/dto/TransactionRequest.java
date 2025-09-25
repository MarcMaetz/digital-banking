package com.example.demo.dto;

import com.example.demo.entity.Account;
import com.example.demo.entity.Transaction;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Request object for processing banking transactions")
public class TransactionRequest {
    
    @NotBlank(message = "From account number is required")
    @Schema(description = "Account number to withdraw from. For DEPOSIT and WITHDRAWAL, use the same account number as toAccountNumber", 
            example = "ACC001")
    private String fromAccountNumber;
    
    @NotBlank(message = "To account number is required")
    @Schema(description = "Account number to deposit to. For DEPOSIT and WITHDRAWAL, use the same account number as fromAccountNumber", 
            example = "ACC001")
    private String toAccountNumber;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    @Schema(description = "Transaction amount", example = "100.00")
    private BigDecimal amount;
    
    @NotNull(message = "Transaction type is required")
    @Schema(description = "Type of transaction", 
            example = "DEPOSIT",
            allowableValues = {"DEPOSIT", "WITHDRAWAL", "TRANSFER", "PAYMENT", "REFUND"})
    private Transaction.TransactionType type;
    
    @Schema(description = "Optional description of the transaction", example = "ATM withdrawal")
    private String description;
}
