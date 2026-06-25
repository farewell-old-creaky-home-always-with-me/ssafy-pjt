package com.ssafy.home.external.seoul.demographics;

public class SeoulDemographicsApiException extends RuntimeException {

    private final boolean retryable;

    public SeoulDemographicsApiException(String message) {
        super(message);
        this.retryable = false;
    }

    public SeoulDemographicsApiException(String message, Throwable cause, boolean retryable) {
        super(message, cause);
        this.retryable = retryable;
    }

    public boolean retryable() {
        return retryable;
    }
}
