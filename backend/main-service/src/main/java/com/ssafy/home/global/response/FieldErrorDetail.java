package com.ssafy.home.global.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class FieldErrorDetail {

    private final String field;
    private final String message;

    public static FieldErrorDetail of(String field, String message) {
        return new FieldErrorDetail(field, message);
    }
}
