package com.ssafy.home.global.response;

import java.util.List;

public class ErrorDetail {

    private final String code;
    private final String message;
    private final List<FieldErrorDetail> fields;

    private ErrorDetail(String code, String message, List<FieldErrorDetail> fields) {
        this.code = code;
        this.message = message;
        this.fields = fields;
    }

    public static ErrorDetail of(String code, String message) {
        return new ErrorDetail(code, message, null);
    }

    public static ErrorDetail of(String code, String message, List<FieldErrorDetail> fields) {
        return new ErrorDetail(code, message, fields);
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public List<FieldErrorDetail> getFields() {
        return fields;
    }
}
