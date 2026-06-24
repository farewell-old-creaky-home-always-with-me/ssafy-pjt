package com.ssafy.home.batch.processor;

public class InvalidHouseDealException extends RuntimeException {

    private final String reason;

    public InvalidHouseDealException(String message) {
        this("INVALID_HOUSE_DEAL", message);
    }

    public InvalidHouseDealException(String reason, String message) {
        super(message);
        this.reason = reason;
    }

    public InvalidHouseDealException(String message, Throwable cause) {
        this("INVALID_HOUSE_DEAL", message, cause);
    }

    public InvalidHouseDealException(String reason, String message, Throwable cause) {
        super(message, cause);
        this.reason = reason;
    }

    public String reason() {
        return reason;
    }
}
