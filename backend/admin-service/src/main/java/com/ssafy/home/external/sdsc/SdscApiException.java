package com.ssafy.home.external.sdsc;

public class SdscApiException extends RuntimeException {

    private final boolean retryable;

    public SdscApiException(String message, Throwable cause, boolean retryable) {
        super(message, cause);
        this.retryable = retryable;
    }

    public SdscApiException(String message) {
        this(message, null, false);
    }

    public boolean retryable() {
        return retryable;
    }
}
