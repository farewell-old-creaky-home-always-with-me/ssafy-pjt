package com.ssafy.home.global.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorDetail {

    private final String code;
    private final String message;

    public static ErrorDetail of(String code, String message) {
        return new ErrorDetail(code, message);
    }
}
