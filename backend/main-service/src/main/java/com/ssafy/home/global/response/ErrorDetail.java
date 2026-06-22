package com.ssafy.home.global.response;

import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorDetail {

    private final String code;
    private final String message;
    private final List<FieldErrorDetail> fields;

    public static ErrorDetail of(String code, String message) {
        return new ErrorDetail(code, message, null);
    }

    public static ErrorDetail of(String code, String message, List<FieldErrorDetail> fields) {
        return new ErrorDetail(code, message, fields);
    }
}
