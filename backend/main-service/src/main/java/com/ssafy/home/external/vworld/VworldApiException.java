package com.ssafy.home.external.vworld;

public class VworldApiException extends RuntimeException {

    private final boolean retryable;

    public VworldApiException(String message, Throwable cause, boolean retryable) {
        super(message, cause);
        this.retryable = retryable;
    }

    public VworldApiException(String message) {
        this(message, null, false);
    }

    public boolean retryable() {
        return retryable;
    }
}
