package com.example;

public class ContestException extends Exception {
    public ContestException() {
    }

    public ContestException(String message) {
        super(message);
    }

    public ContestException(String message, Throwable cause) {
        super(message, cause);
    }
}
