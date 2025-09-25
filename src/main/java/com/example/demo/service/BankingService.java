package com.example.demo.service;

import com.example.demo.dto.CreateAccountRequest;
import com.example.demo.dto.TransactionRequest;
import com.example.demo.entity.Account;
import com.example.demo.entity.Transaction;
import com.example.demo.exception.AccountNotFoundException;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BankingService {
    
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    
    public Account createAccount(CreateAccountRequest request) {
        log.info("Creating new account for customer: {}", request.getCustomerName());
        
        if (accountRepository.existsByAccountNumber(request.getAccountNumber())) {
            throw new IllegalArgumentException("Account number already exists: " + request.getAccountNumber());
        }
        
        Account account = new Account();
        account.setAccountNumber(request.getAccountNumber());
        account.setCustomerName(request.getCustomerName());
        account.setEmail(request.getEmail());
        account.setAccountType(request.getAccountType());
        account.setBalance(request.getInitialBalance());
        account.setStatus(Account.AccountStatus.ACTIVE);
        
        Account savedAccount = accountRepository.save(account);
        log.info("Account created successfully with ID: {}", savedAccount.getId());
        
        return savedAccount;
    }
    
    public Account getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNumber));
    }
    
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }
    
    public List<Account> getAccountsByCustomerName(String customerName) {
        return accountRepository.findByCustomerNameContainingIgnoreCase(customerName);
    }
    
    public Transaction processTransaction(TransactionRequest request) {
        log.info("Processing transaction: {} from {} to {}", 
                request.getType(), request.getFromAccountNumber(), request.getToAccountNumber());
        
        Account fromAccount = getAccountByNumber(request.getFromAccountNumber());
        Account toAccount = getAccountByNumber(request.getToAccountNumber());
        
        // Validate account status
        if (fromAccount.getStatus() != Account.AccountStatus.ACTIVE) {
            throw new IllegalStateException("From account is not active: " + request.getFromAccountNumber());
        }
        
        if (toAccount.getStatus() != Account.AccountStatus.ACTIVE) {
            throw new IllegalStateException("To account is not active: " + request.getToAccountNumber());
        }
        
        // Validate sufficient balance for withdrawals and transfers
        if ((request.getType() == Transaction.TransactionType.WITHDRAWAL || 
             request.getType() == Transaction.TransactionType.TRANSFER) &&
            fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new IllegalStateException("Insufficient balance in account: " + request.getFromAccountNumber());
        }
        
        Transaction transaction = new Transaction();
        transaction.setTransactionReference(UUID.randomUUID().toString());
        transaction.setAmount(request.getAmount());
        transaction.setType(request.getType());
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);
        transaction.setDescription(request.getDescription());
        transaction.setStatus(Transaction.TransactionStatus.PENDING);
        
        try {
            // Process the transaction
            switch (request.getType()) {
                case DEPOSIT:
                    toAccount.setBalance(toAccount.getBalance().add(request.getAmount()));
                    break;
                case WITHDRAWAL:
                    fromAccount.setBalance(fromAccount.getBalance().subtract(request.getAmount()));
                    break;
                case TRANSFER:
                    fromAccount.setBalance(fromAccount.getBalance().subtract(request.getAmount()));
                    toAccount.setBalance(toAccount.getBalance().add(request.getAmount()));
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported transaction type: " + request.getType());
            }
            
            // Save accounts
            accountRepository.save(fromAccount);
            accountRepository.save(toAccount);
            
            // Mark transaction as completed
            transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
            transaction.setProcessedAt(LocalDateTime.now());
            
            Transaction savedTransaction = transactionRepository.save(transaction);
            log.info("Transaction processed successfully: {}", savedTransaction.getTransactionReference());
            
            return savedTransaction;
            
        } catch (Exception e) {
            log.error("Transaction failed: {}", e.getMessage());
            transaction.setStatus(Transaction.TransactionStatus.FAILED);
            transactionRepository.save(transaction);
            throw new RuntimeException("Transaction processing failed: " + e.getMessage(), e);
        }
    }
    
    public List<Transaction> getAccountTransactions(String accountNumber) {
        Account account = getAccountByNumber(accountNumber);
        return transactionRepository.findTransactionsByAccountId(account.getId());
    }
    
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
    
    public BigDecimal getAccountBalance(String accountNumber) {
        Account account = getAccountByNumber(accountNumber);
        return account.getBalance();
    }
    
    public Account updateAccountStatus(String accountNumber, Account.AccountStatus status) {
        Account account = getAccountByNumber(accountNumber);
        account.setStatus(status);
        return accountRepository.save(account);
    }
}
