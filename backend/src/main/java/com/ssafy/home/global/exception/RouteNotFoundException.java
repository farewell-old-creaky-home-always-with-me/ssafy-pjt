package com.ssafy.home.global.exception;

import org.springframework.http.HttpStatus;

public class RouteNotFoundException extends BusinessException {

    public RouteNotFoundException(String code, String message) {
        super(code, message, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
