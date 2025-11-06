package com.example.demo.exception;

public class ConcurrentTransactionException extends RuntimeException {

    public ConcurrentTransactionException(String message, Throwable cause) {
        super(message, cause);
    }
}

