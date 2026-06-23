package com.ssafy.home.global.exception;

import com.ssafy.home.global.response.ErrorDetail;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDetail> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.badRequest()
            .body(ErrorDetail.of("INVALID_INPUT", e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetail> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
            .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
            .findFirst()
            .orElse("입력값이 올바르지 않습니다.");
        return ResponseEntity.badRequest()
            .body(ErrorDetail.of("INVALID_INPUT", message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetail> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(ErrorDetail.of("AI_SERVICE_ERROR", "AI 서비스 오류가 발생했습니다. 잠시 후 다시 시도해 주세요."));
    }
}
