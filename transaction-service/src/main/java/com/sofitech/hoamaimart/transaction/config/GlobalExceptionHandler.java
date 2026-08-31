package com.sofitech.hoamaimart.transaction.config;

import com.sofitech.hoamaimart.shared.error.BusinessErrorCode;
import com.sofitech.hoamaimart.shared.error.BusinessException;
import com.sofitech.hoamaimart.shared.error.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * Global Exception Handler cho transaction-service.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(
            BusinessException ex,
            HttpServletRequest request
    ) {
        HttpStatus status = mapToHttpStatus(ex.getErrorCode());
        log.warn("[{}] {}", ex.getErrorCode().getFormattedCode(), ex.getMessage());
        return ResponseEntity.status(status).body(
                ErrorResponse.of(ex.getErrorCode(), ex.getMessage(), request.getRequestURI())
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest().body(
                ErrorResponse.of(BusinessErrorCode.COMMON_VALIDATION_ERROR, message, request.getRequestURI())
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraint(
            ConstraintViolationException ex,
            HttpServletRequest request
    ) {
        return ResponseEntity.badRequest().body(
                ErrorResponse.of(BusinessErrorCode.COMMON_VALIDATION_ERROR, ex.getMessage(), request.getRequestURI())
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request
    ) {
        return ResponseEntity.badRequest().body(
                ErrorResponse.of(BusinessErrorCode.COMMON_VALIDATION_ERROR, ex.getMessage(), request.getRequestURI())
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(
            Exception ex,
            HttpServletRequest request
    ) {
        log.error("Unhandled", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ErrorResponse.of(BusinessErrorCode.COMMON_INTERNAL_ERROR, ex.getMessage(), request.getRequestURI())
        );
    }

    private HttpStatus mapToHttpStatus(BusinessErrorCode code) {
        return switch (code) {
            case CUSTOMER_NOT_FOUND, TRANSACTION_NOT_FOUND, COMMON_RESOURCE_NOT_FOUND,
                 LOYALTY_ACCOUNT_NOT_FOUND, LOYALTY_TIER_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case CUSTOMER_INACTIVE, CUSTOMER_DUPLICATE_PHONE, CUSTOMER_DUPLICATE_EMAIL,
                 POS_DUPLICATE_TRANSACTION -> HttpStatus.CONFLICT;
            case POS_INVALID_AMOUNT, LOYALTY_INSUFFICIENT_POINTS, LOYALTY_INVALID_POINTS,
                 LOYALTY_TIER_INVALID, COMMON_VALIDATION_ERROR -> HttpStatus.BAD_REQUEST;
            case COMMON_INTERNAL_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}