package com.team5.web_ide.global.exception;

import com.team5.web_ide.global.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Void>> handleApiException(ApiException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        return ResponseEntity.status(errorCode.status())
                .body(ApiResponse.fail(errorCode.code(), errorCode.message()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage() == null ? GlobalErrorCode.VALIDATION_ERROR.message() : error.getDefaultMessage())
                .orElse(GlobalErrorCode.VALIDATION_ERROR.message());

        return ResponseEntity.status(GlobalErrorCode.VALIDATION_ERROR.status())
                .body(ApiResponse.fail(GlobalErrorCode.VALIDATION_ERROR.code(), message));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(GlobalErrorCode.BAD_REQUEST.status())
                .body(ApiResponse.fail(GlobalErrorCode.BAD_REQUEST.code(), ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception ex) {
        log.error("Unhandled exception", ex);
        return ResponseEntity.status(GlobalErrorCode.INTERNAL_SERVER_ERROR.status())
                .body(ApiResponse.fail(GlobalErrorCode.INTERNAL_SERVER_ERROR.code(), GlobalErrorCode.INTERNAL_SERVER_ERROR.message()));
    }
}
