package com.example.demo.controller;

import com.example.demo.dto.TransactionRequest;
import com.example.demo.entity.Transaction;
import com.example.demo.service.BankingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {
    
    private final BankingService bankingService;
    
    @PostMapping
    public ResponseEntity<Transaction> processTransaction(@Valid @RequestBody TransactionRequest request) {
        log.info("Processing transaction request: {}", request.getType());
        Transaction transaction = bankingService.processTransaction(request);
        return ResponseEntity.ok(transaction);
    }
    
    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = bankingService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/account/{accountNumber}")
    public ResponseEntity<List<Transaction>> getTransactionsByAccount(@PathVariable String accountNumber) {
        List<Transaction> transactions = bankingService.getAccountTransactions(accountNumber);
        return ResponseEntity.ok(transactions);
    }
}
