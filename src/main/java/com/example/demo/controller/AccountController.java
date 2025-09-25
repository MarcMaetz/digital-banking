package com.example.demo.controller;

import com.example.demo.dto.CreateAccountRequest;
import com.example.demo.entity.Account;
import com.example.demo.service.BankingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Account Management", description = "APIs for managing bank accounts")
public class AccountController {
    
    private final BankingService bankingService;
    
    @PostMapping
    @Operation(summary = "Create a new account", description = "Creates a new bank account with the provided details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Account created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Account number already exists")
    })
    public ResponseEntity<Account> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        log.info("Creating account for customer: {}", request.getCustomerName());
        Account account = bankingService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(account);
    }
    
    @GetMapping("/{accountNumber}")
    @Operation(summary = "Get account by number", description = "Retrieves account details by account number")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Account found"),
        @ApiResponse(responseCode = "404", description = "Account not found")
    })
    public ResponseEntity<Account> getAccount(
            @Parameter(description = "Account number") @PathVariable String accountNumber) {
        Account account = bankingService.getAccountByNumber(accountNumber);
        return ResponseEntity.ok(account);
    }
    
    @GetMapping
    @Operation(summary = "Get all accounts", description = "Retrieves all bank accounts")
    public ResponseEntity<List<Account>> getAllAccounts() {
        List<Account> accounts = bankingService.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search accounts by customer name", description = "Searches accounts by customer name")
    public ResponseEntity<List<Account>> searchAccountsByCustomerName(
            @Parameter(description = "Customer name to search") @RequestParam String customerName) {
        List<Account> accounts = bankingService.getAccountsByCustomerName(customerName);
        return ResponseEntity.ok(accounts);
    }
    
    @GetMapping("/{accountNumber}/balance")
    @Operation(summary = "Get account balance", description = "Retrieves the current balance of an account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Balance retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Account not found")
    })
    public ResponseEntity<BigDecimal> getAccountBalance(
            @Parameter(description = "Account number") @PathVariable String accountNumber) {
        BigDecimal balance = bankingService.getAccountBalance(accountNumber);
        return ResponseEntity.ok(balance);
    }
    
    @GetMapping("/{accountNumber}/transactions")
    @Operation(summary = "Get account transactions", description = "Retrieves all transactions for an account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Account not found")
    })
    public ResponseEntity<List<com.example.demo.entity.Transaction>> getAccountTransactions(
            @Parameter(description = "Account number") @PathVariable String accountNumber) {
        List<com.example.demo.entity.Transaction> transactions = bankingService.getAccountTransactions(accountNumber);
        return ResponseEntity.ok(transactions);
    }
    
    @PutMapping("/{accountNumber}/status")
    @Operation(summary = "Update account status", description = "Updates the status of an account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status updated successfully"),
        @ApiResponse(responseCode = "404", description = "Account not found")
    })
    public ResponseEntity<Account> updateAccountStatus(
            @Parameter(description = "Account number") @PathVariable String accountNumber,
            @Parameter(description = "New account status") @RequestParam Account.AccountStatus status) {
        Account account = bankingService.updateAccountStatus(accountNumber, status);
        return ResponseEntity.ok(account);
    }
}
