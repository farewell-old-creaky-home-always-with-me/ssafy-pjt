package com.ssafy.home.batch.processor;

public class InvalidForeignResidentException extends RuntimeException {
    public InvalidForeignResidentException(String message) {
        super(message);
    }
}
