package com.ssafy.home.global.response;

public class FieldErrorDetail {

    private final String field;
    private final String message;

    private FieldErrorDetail(String field, String message) {
        this.field = field;
        this.message = message;
    }

    public static FieldErrorDetail of(String field, String message) {
        return new FieldErrorDetail(field, message);
    }

    public String getField() {
        return field;
    }

    public String getMessage() {
        return message;
    }
}
