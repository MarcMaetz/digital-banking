package com.example.demo.dto;

import com.example.demo.entity.Account;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateAccountRequest {
    
    @NotBlank
    private String accountNumber;
    
    @NotBlank
    private String customerName;
    
    @NotBlank
    @Email
    private String email;
    
    @NotNull
    private Account.AccountType accountType;
    
    @DecimalMin(value = "0.0", message = "Initial balance cannot be negative")
    private BigDecimal initialBalance = BigDecimal.ZERO;
}
