package com.ssafy.home.external.seoul.cctv;

public class SeoulCctvApiException extends RuntimeException {

    private final boolean retryable;

    public SeoulCctvApiException(String message) {
        super(message);
        this.retryable = false;
    }

    public SeoulCctvApiException(String message, Throwable cause, boolean retryable) {
        super(message, cause);
        this.retryable = retryable;
    }

    public boolean retryable() {
        return retryable;
    }
}
