package com.example.demo.controller;

import com.example.demo.dto.CreateAccountRequest;
import com.example.demo.entity.Account;
import com.example.demo.entity.Transaction;
import com.example.demo.service.BankingService;
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
public class AccountController {
    
    private final BankingService bankingService;
    
    @PostMapping
    public ResponseEntity<Account> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        log.info("Creating account for customer: {}", request.getCustomerName());
        Account account = bankingService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(account);
    }
    
    @GetMapping("/{accountNumber}")
    public ResponseEntity<Account> getAccount(@PathVariable String accountNumber) {
        Account account = bankingService.getAccountByNumber(accountNumber);
        return ResponseEntity.ok(account);
    }
    
    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {
        List<Account> accounts = bankingService.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Account>> searchAccountsByCustomerName(@RequestParam String customerName) {
        List<Account> accounts = bankingService.getAccountsByCustomerName(customerName);
        return ResponseEntity.ok(accounts);
    }
    
    @GetMapping("/{accountNumber}/balance")
    public ResponseEntity<BigDecimal> getAccountBalance(@PathVariable String accountNumber) {
        BigDecimal balance = bankingService.getAccountBalance(accountNumber);
        return ResponseEntity.ok(balance);
    }
    
    @GetMapping("/{accountNumber}/transactions")
    public ResponseEntity<List<Transaction>> getAccountTransactions(@PathVariable String accountNumber) {
        List<Transaction> transactions = bankingService.getAccountTransactions(accountNumber);
        return ResponseEntity.ok(transactions);
    }
    
    @PutMapping("/{accountNumber}/status")
    public ResponseEntity<Account> updateAccountStatus(
            @PathVariable String accountNumber,
            @RequestParam Account.AccountStatus status) {
        Account account = bankingService.updateAccountStatus(accountNumber, status);
        return ResponseEntity.ok(account);
    }
}
