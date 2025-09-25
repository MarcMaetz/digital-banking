package com.example.demo.config;

import com.example.demo.entity.Account;
import com.example.demo.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final AccountRepository accountRepository;
    
    @Override
    public void run(String... args) throws Exception {
        log.info("Initializing sample data...");
        
        // Create sample accounts
        if (accountRepository.count() == 0) {
            createSampleAccounts();
            log.info("Sample data initialized successfully!");
        } else {
            log.info("Sample data already exists, skipping initialization.");
        }
    }
    
    private void createSampleAccounts() {
        // Sample checking account
        Account checkingAccount = new Account();
        checkingAccount.setAccountNumber("CHK001");
        checkingAccount.setCustomerName("John Doe");
        checkingAccount.setEmail("john.doe@email.com");
        checkingAccount.setAccountType(Account.AccountType.CHECKING);
        checkingAccount.setBalance(new BigDecimal("5000.00"));
        checkingAccount.setStatus(Account.AccountStatus.ACTIVE);
        accountRepository.save(checkingAccount);
        
        // Sample savings account
        Account savingsAccount = new Account();
        savingsAccount.setAccountNumber("SAV001");
        savingsAccount.setCustomerName("Jane Smith");
        savingsAccount.setEmail("jane.smith@email.com");
        savingsAccount.setAccountType(Account.AccountType.SAVINGS);
        savingsAccount.setBalance(new BigDecimal("15000.00"));
        savingsAccount.setStatus(Account.AccountStatus.ACTIVE);
        accountRepository.save(savingsAccount);
        
        // Sample business account
        Account businessAccount = new Account();
        businessAccount.setAccountNumber("BUS001");
        businessAccount.setCustomerName("Tech Startup GmbH");
        businessAccount.setEmail("finance@techstartup.com");
        businessAccount.setAccountType(Account.AccountType.BUSINESS);
        businessAccount.setBalance(new BigDecimal("50000.00"));
        businessAccount.setStatus(Account.AccountStatus.ACTIVE);
        accountRepository.save(businessAccount);
        
        // Sample investment account
        Account investmentAccount = new Account();
        investmentAccount.setAccountNumber("INV001");
        investmentAccount.setCustomerName("Robert Johnson");
        investmentAccount.setEmail("robert.johnson@email.com");
        investmentAccount.setAccountType(Account.AccountType.INVESTMENT);
        investmentAccount.setBalance(new BigDecimal("100000.00"));
        investmentAccount.setStatus(Account.AccountStatus.ACTIVE);
        accountRepository.save(investmentAccount);
        
        log.info("Created 4 sample accounts");
    }
}
