package com.ssafy.home.global.exception;

import com.ssafy.home.global.response.ApiResponse;
import com.ssafy.home.global.response.ErrorDetail;
import com.ssafy.home.global.response.FieldErrorDetail;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
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

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustom(CustomException ex) {
        ErrorCode code = ex.getErrorCode();
        return ResponseEntity.status(code.getStatus())
                .body(ApiResponse.error(ErrorDetail.of(code.name(), code.getMessage())));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        List<FieldErrorDetail> fields = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::toFieldErrorDetail)
                .toList();

        ErrorCode code = ErrorCode.COMMON_INVALID_INPUT;
        return ResponseEntity.status(code.getStatus())
                .body(ApiResponse.error(ErrorDetail.of(code.name(), code.getMessage(), fields)));
    }

    @ExceptionHandler({
            ConstraintViolationException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(Exception ex) {
        ErrorCode code = ErrorCode.COMMON_INVALID_INPUT;
        return ResponseEntity.status(code.getStatus())
                .body(ApiResponse.error(ErrorDetail.of(code.name(), code.getMessage())));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        ErrorCode code = ErrorCode.COMMON_DATA_CONFLICT;
        return ResponseEntity.status(code.getStatus())
                .body(ApiResponse.error(ErrorDetail.of(code.name(), code.getMessage())));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneral(Exception ex) {
        ErrorCode code = ErrorCode.COMMON_INTERNAL_ERROR;
        return ResponseEntity.status(code.getStatus())
                .body(ApiResponse.error(ErrorDetail.of(code.name(), code.getMessage())));
    }

    private FieldErrorDetail toFieldErrorDetail(FieldError fieldError) {
        return FieldErrorDetail.of(fieldError.getField(), fieldError.getDefaultMessage());
    }
}
