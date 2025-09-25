package com.example.demo.dto;

import com.example.demo.entity.Account;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateAccountRequest {
    
    @NotBlank(message = "Account number is required")
    private String accountNumber;
    
    @NotBlank(message = "Customer name is required")
    private String customerName;
    
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;
    
    @NotNull(message = "Account type is required")
    private Account.AccountType accountType;
    
    private BigDecimal initialBalance = BigDecimal.ZERO;
}
