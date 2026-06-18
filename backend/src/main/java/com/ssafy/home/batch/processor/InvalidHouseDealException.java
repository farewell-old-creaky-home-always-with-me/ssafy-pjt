package com.ssafy.home.batch.processor;

public class InvalidHouseDealException extends RuntimeException {
    public InvalidHouseDealException(String message) {
        super(message);
    }

    public InvalidHouseDealException(String message, Throwable cause) {
        super(message, cause);
    }
}
