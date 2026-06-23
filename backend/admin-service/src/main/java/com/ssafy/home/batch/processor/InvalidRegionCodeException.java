package com.ssafy.home.batch.processor;

public class InvalidRegionCodeException extends RuntimeException {

    public InvalidRegionCodeException(String message) {
        super(message);
    }
}
