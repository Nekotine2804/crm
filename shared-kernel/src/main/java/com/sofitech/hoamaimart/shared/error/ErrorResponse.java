package com.sofitech.hoamaimart.shared.error;

import java.time.Instant;

/**
 * Standardized Error Response theo format BRD:
 * {
 *   "code": "LOYALTY_001",
 *   "message": "Insufficient loyalty points",
 *   "timestamp": "2026-08-31T10:00:00",
 *   "path": "/api/v1/customers/1/loyalty/redeem"
 * }
 */
public record ErrorResponse(
        String code,
        String message,
        Instant timestamp,
        String path
) {
    public static ErrorResponse of(BusinessErrorCode errorCode, String message, String path) {
        return new ErrorResponse(
                errorCode.getFormattedCode(),
                message,
                Instant.now(),
                path
        );
    }

    public static ErrorResponse of(BusinessErrorCode errorCode, String path) {
        return new ErrorResponse(
                errorCode.getFormattedCode(),
                errorCode.getDefaultMessage(),
                Instant.now(),
                path
        );
    }
}