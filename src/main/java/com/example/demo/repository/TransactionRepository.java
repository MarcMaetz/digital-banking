package com.example.demo.repository;

import com.example.demo.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    Optional<Transaction> findByTransactionReference(String transactionReference);
    
    List<Transaction> findByFromAccountId(Long fromAccountId);
    
    List<Transaction> findByToAccountId(Long toAccountId);
    
    List<Transaction> findByType(Transaction.TransactionType type);
    
    List<Transaction> findByStatus(Transaction.TransactionStatus status);
    
    List<Transaction> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT t FROM Transaction t WHERE (t.fromAccount.id = :accountId OR t.toAccount.id = :accountId) ORDER BY t.createdAt DESC")
    List<Transaction> findTransactionsByAccountId(@Param("accountId") Long accountId);
    
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.fromAccount.id = :accountId AND t.type = :type AND t.status = :status")
    java.math.BigDecimal getTotalAmountByAccountAndTypeAndStatus(
        @Param("accountId") Long accountId, 
        @Param("type") Transaction.TransactionType type, 
        @Param("status") Transaction.TransactionStatus status
    );
}
