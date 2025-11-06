package com.example.demo.service;

import com.example.demo.dto.CreateAccountRequest;
import com.example.demo.dto.TransactionRequest;
import com.example.demo.dto.TransactionType;
import com.example.demo.entity.Account;
import com.example.demo.entity.Transaction;
import com.example.demo.exception.AccountNotFoundException;
import com.example.demo.exception.InsufficientBalanceException;
import com.example.demo.exception.ConcurrentTransactionException;
import com.example.demo.mapper.AccountMapper;
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
    private final AccountMapper accountMapper;
    
    public Account createAccount(CreateAccountRequest request) {
        log.info("Creating new account for customer: {}", request.getCustomerName());
        
        if (accountRepository.existsByAccountNumber(request.getAccountNumber())) {
            throw new IllegalArgumentException("Account number already exists: " + request.getAccountNumber());
        }

        Account account = accountMapper.toAccount(request);
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
            Account fromAccount = null;
            Account toAccount = null;
            
            // Fetch and validate accounts based on transaction type requirements
            switch (request.getType()) {
                case DEPOSIT:
                    // DEPOSIT only requires toAccount
                    toAccount = getAccountByNumber(request.getToAccountNumber());
                    validateAccountStatus(toAccount, "To account");
                    break;
                    
                case WITHDRAWAL:
                    // WITHDRAWAL only requires fromAccount
                    fromAccount = getAccountByNumber(request.getFromAccountNumber());
                    validateAccountStatus(fromAccount, "From account");
                    validateSufficientBalance(fromAccount, request.getAmount());
                    break;
                    
                case TRANSFER:
                    // TRANSFER requires both accounts
                    fromAccount = getAccountByNumber(request.getFromAccountNumber());
                    toAccount = getAccountByNumber(request.getToAccountNumber());
                    validateAccountStatus(fromAccount, "From account");
                    validateAccountStatus(toAccount, "To account");
                    validateSufficientBalance(fromAccount, request.getAmount());
                    // Business rule: Cannot transfer to the same account
                    if (request.getFromAccountNumber().equals(request.getToAccountNumber())) {
                        throw new IllegalArgumentException("Cannot transfer to the same account");
                    }
                    break;
                    
                case PAYMENT:
                case REFUND:
                    // PAYMENT and REFUND require both accounts
                    fromAccount = getAccountByNumber(request.getFromAccountNumber());
                    toAccount = getAccountByNumber(request.getToAccountNumber());
                    validateAccountStatus(fromAccount, "From account");
                    validateAccountStatus(toAccount, "To account");
                    validateSufficientBalance(fromAccount, request.getAmount());
                    break;
                    
                default:
                    throw new IllegalArgumentException("Unsupported transaction type: " + request.getType());
            }
        
        // Create transaction record
        Transaction transaction = new Transaction();
        transaction.setTransactionReference(UUID.randomUUID().toString());
        transaction.setAmount(request.getAmount());
        transaction.setType(toEntityTransactionType(request.getType()));
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);
        transaction.setDescription(request.getDescription());
        transaction.setStatus(Transaction.TransactionStatus.PENDING);
        
        // Process the transaction - Spring will handle rollback automatically if any exception occurs
        switch (request.getType()) {
            case DEPOSIT:
                assert toAccount != null;
                toAccount.setBalance(toAccount.getBalance().add(request.getAmount()));
                accountRepository.save(toAccount);
                break;
            case WITHDRAWAL:
                assert fromAccount != null;
                fromAccount.setBalance(fromAccount.getBalance().subtract(request.getAmount()));
                accountRepository.save(fromAccount);
                break;
            case TRANSFER, PAYMENT, REFUND:
                assert fromAccount != null;
                fromAccount.setBalance(fromAccount.getBalance().subtract(request.getAmount()));
                assert toAccount != null;
                toAccount.setBalance(toAccount.getBalance().add(request.getAmount()));
                accountRepository.save(fromAccount);
                accountRepository.save(toAccount);
                break;
            default:
                throw new IllegalArgumentException("Unsupported transaction type: " + request.getType());
        }
        
        // Mark transaction as completed
        transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
        transaction.setProcessedAt(LocalDateTime.now());
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("Transaction processed successfully: {}", savedTransaction.getTransactionReference());
        
        return savedTransaction;
        
        } catch (ObjectOptimisticLockingFailureException e) {
            log.error("Optimistic locking failure during transaction processing: {}", e.getMessage());
            throw new ConcurrentTransactionException("Transaction failed due to concurrent modification. Please retry.", e);
        } catch (Exception e) {
            log.error("Error processing transaction: {}", e.getMessage(), e);
            throw e; // Re-throw to trigger transaction rollback
        }
    }
    
    private void validateAccountStatus(Account account, String accountLabel) {
        if (account.getStatus() != Account.AccountStatus.ACTIVE) {
            throw new IllegalStateException(accountLabel + " is not active: " + account.getAccountNumber());
        }
    }
    
    private void validateSufficientBalance(Account account, BigDecimal amount) {
        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance in account: " + account.getAccountNumber());
        }
    }
    
    private Transaction.TransactionType toEntityTransactionType(TransactionType dtoType) {
        return switch (dtoType) {
            case DEPOSIT -> Transaction.TransactionType.DEPOSIT;
            case WITHDRAWAL -> Transaction.TransactionType.WITHDRAWAL;
            case TRANSFER -> Transaction.TransactionType.TRANSFER;
            case PAYMENT -> Transaction.TransactionType.PAYMENT;
            case REFUND -> Transaction.TransactionType.REFUND;
        };
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
