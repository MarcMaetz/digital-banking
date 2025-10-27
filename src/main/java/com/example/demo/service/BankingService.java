package com.example.demo.service;

import com.example.demo.dto.CreateAccountRequest;
import com.example.demo.dto.TransactionRequest;
import com.example.demo.entity.Account;
import com.example.demo.entity.Transaction;
import com.example.demo.exception.AccountNotFoundException;
import com.example.demo.exception.InsufficientBalanceException;
import com.example.demo.exception.ConcurrentModificationException;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class BankingService {
    
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    
    public Account createAccount(CreateAccountRequest request) {
        log.info("Creating new account for customer: {}", request.getCustomerName());
        
        if (accountRepository.existsByAccountNumber(request.getAccountNumber())) {
            throw new IllegalArgumentException("Account number already exists: " + request.getAccountNumber());
        }

        //@TODO do it with map struct
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
    
    @Transactional(readOnly = true)
    public Account getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNumber));
    }
    
    @Transactional(readOnly = true)
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public List<Account> getAccountsByCustomerName(String customerName) {
        return accountRepository.findByCustomerNameContainingIgnoreCase(customerName);
    }
    
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class, timeout = 30)
    public Transaction processTransaction(TransactionRequest request) {
        log.info("Processing transaction: {} from {} to {}", 
                request.getType(), request.getFromAccountNumber(), request.getToAccountNumber());
        
        try {
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
            throw new InsufficientBalanceException("Insufficient balance in account: " + request.getFromAccountNumber());
        }
        
        // Business rule: Cannot transfer to the same account
        if (request.getType() == Transaction.TransactionType.TRANSFER && 
            request.getFromAccountNumber().equals(request.getToAccountNumber())) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }
        
        // Create transaction record
        Transaction transaction = new Transaction();
        transaction.setTransactionReference(UUID.randomUUID().toString());
        transaction.setAmount(request.getAmount());
        transaction.setType(request.getType());
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);
        transaction.setDescription(request.getDescription());
        transaction.setStatus(Transaction.TransactionStatus.PENDING);
        
        // Process the transaction - Spring will handle rollback automatically if any exception occurs
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
        
        // Save accounts - Spring transaction will ensure atomicity
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
        
        // Mark transaction as completed
        transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
        transaction.setProcessedAt(LocalDateTime.now());
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("Transaction processed successfully: {}", savedTransaction.getTransactionReference());
        
        return savedTransaction;
        
        } catch (ObjectOptimisticLockingFailureException e) {
            log.error("Optimistic locking failure during transaction processing: {}", e.getMessage());
            throw new ConcurrentModificationException("Transaction failed due to concurrent modification. Please retry.", e);
        } catch (Exception e) {
            log.error("Error processing transaction: {}", e.getMessage(), e);
            throw e; // Re-throw to trigger transaction rollback
        }
    }
    
    @Transactional(readOnly = true)
    public List<Transaction> getAccountTransactions(String accountNumber) {
        Account account = getAccountByNumber(accountNumber);
        return transactionRepository.findTransactionsByAccountId(account.getId());
    }
    
    @Transactional(readOnly = true)
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public BigDecimal getAccountBalance(String accountNumber) {
        Account account = getAccountByNumber(accountNumber);
        return account.getBalance();
    }
    
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Account updateAccountStatus(String accountNumber, Account.AccountStatus status) {
        Account account = getAccountByNumber(accountNumber);
        account.setStatus(status);
        return accountRepository.save(account);
    }
}
