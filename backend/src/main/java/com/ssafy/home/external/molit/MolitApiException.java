package com.ssafy.home.external.molit;

public class MolitApiException extends RuntimeException {

    private final String resultCode;
    private final String resultMessage;
    private final boolean retryable;

    public MolitApiException(String resultCode, String resultMessage) {
        super("MOLIT API error [" + resultCode + "]: " + resultMessage);
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
        this.retryable = false;
    }

    public MolitApiException(String message, Throwable cause, boolean retryable) {
        super(message, cause);
        this.resultCode = null;
        this.resultMessage = message;
        this.retryable = retryable;
    }

    public String resultCode() {
        return resultCode;
    }

    public String resultMessage() {
        return resultMessage;
    }

    public boolean retryable() {
        return retryable;
    }
}
