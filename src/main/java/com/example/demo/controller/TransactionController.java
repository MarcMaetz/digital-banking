package com.example.demo.controller;

import com.example.demo.dto.TransactionRequest;
import com.example.demo.entity.Transaction;
import com.example.demo.service.BankingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Transaction Management", description = "APIs for processing banking transactions")
public class TransactionController {
    
    private final BankingService bankingService;
    
    @PostMapping
    @Operation(summary = "Process a transaction", description = "Processes a banking transaction (deposit, withdrawal, transfer)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transaction processed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid transaction data"),
        @ApiResponse(responseCode = "404", description = "Account not found"),
        @ApiResponse(responseCode = "409", description = "Insufficient balance or account not active")
    })
    public ResponseEntity<Transaction> processTransaction(@Valid @RequestBody TransactionRequest request) {
        log.info("Processing transaction request: {}", request.getType());
        Transaction transaction = bankingService.processTransaction(request);
        return ResponseEntity.ok(transaction);
    }
    
    @GetMapping
    @Operation(summary = "Get all transactions", description = "Retrieves all transactions in the system")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = bankingService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/account/{accountNumber}")
    @Operation(summary = "Get transactions by account", description = "Retrieves all transactions for a specific account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Account not found")
    })
    public ResponseEntity<List<Transaction>> getTransactionsByAccount(@PathVariable String accountNumber) {
        List<Transaction> transactions = bankingService.getAccountTransactions(accountNumber);
        return ResponseEntity.ok(transactions);
    }
}
