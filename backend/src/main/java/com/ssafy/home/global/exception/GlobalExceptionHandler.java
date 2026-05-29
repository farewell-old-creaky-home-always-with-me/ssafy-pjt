package com.ssafy.home.global.exception;

import com.ssafy.home.global.response.ErrorDetail;
import com.ssafy.home.global.response.FieldErrorDetail;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorDetail> handleBusiness(BusinessException ex) {
        return ResponseEntity.status(ex.getStatus())
                .body(ErrorDetail.of(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetail> handleValidation(MethodArgumentNotValidException ex) {
        List<FieldErrorDetail> fields = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::toFieldErrorDetail)
                .toList();

        return ResponseEntity.badRequest()
                .body(ErrorDetail.of("COMMON_INVALID_INPUT", "입력값이 올바르지 않습니다", fields));
    }

    @ExceptionHandler({
            ValidationException.class,
            ConstraintViolationException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<ErrorDetail> handleBadRequest(Exception ex) {
        return ResponseEntity.badRequest()
                .body(ErrorDetail.of("COMMON_INVALID_INPUT", "입력값이 올바르지 않습니다"));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorDetail> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorDetail.of("COMMON_INVALID_INPUT", "요청을 처리할 수 없습니다"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetail> handleGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorDetail.of("COMMON_INTERNAL_ERROR", "서버 내부 오류가 발생했습니다"));
    }

    private FieldErrorDetail toFieldErrorDetail(FieldError fieldError) {
        return FieldErrorDetail.of(fieldError.getField(), fieldError.getDefaultMessage());
    }
}
