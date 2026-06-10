package com.ssafy.home.global.exception;

public class DuplicateResourceException extends CustomException {

    public DuplicateResourceException(ErrorCode errorCode) {
        super(errorCode);
    }
}
