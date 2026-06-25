package com.ssafy.home.external.seoul;

public class SeoulEnvironmentApiException extends RuntimeException {

    private final boolean retryable;

    public SeoulEnvironmentApiException(String message) {
        this(message, null, false);
    }

    public SeoulEnvironmentApiException(String message, Throwable cause, boolean retryable) {
        super(message, cause);
        this.retryable = retryable;
    }

    public boolean retryable() {
        return retryable;
    }
}
