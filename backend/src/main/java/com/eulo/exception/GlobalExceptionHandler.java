package com.eulo.exception;

import com.eulo.model.ErrorDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> handleValidation(MethodArgumentNotValidException ex,
                                                      HttpServletRequest req) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .findFirst().orElse("Validation failed");
        log.warn("Validation error on [{}] {}: {}", req.getMethod(), req.getRequestURI(), message);
        return ResponseEntity.badRequest().body(new ErrorDto("VALIDATION_ERROR", message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleGeneral(Exception ex, HttpServletRequest req) {
        log.error("Unhandled error on [{}] {}: {}", req.getMethod(), req.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.status(500).body(new ErrorDto("INTERNAL_ERROR", ex.getMessage()));
    }
}
