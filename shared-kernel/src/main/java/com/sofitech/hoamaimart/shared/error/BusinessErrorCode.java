package com.sofitech.hoamaimart.shared.error;

/**
 * Business Error Code - enum chuẩn hóa mã lỗi cho toàn hệ thống.
 * Format: {SERVICE}_{NUMBER}
 *
 * Loyalty:  LOYALTY_xxx
 * Customer: CUSTOMER_xxx
 * POS:      POS_xxx
 * Generic:  COMMON_xxx
 */
public enum BusinessErrorCode {

    // ===== Generic (1000-1999) =====
    COMMON_VALIDATION_ERROR(1000, "Validation error"),
    COMMON_INTERNAL_ERROR(1001, "Internal server error"),
    COMMON_RESOURCE_NOT_FOUND(1002, "Resource not found"),

    // ===== Customer (2000-2999) =====
    CUSTOMER_NOT_FOUND(2001, "Customer not found"),
    CUSTOMER_INACTIVE(2002, "Customer is inactive"),
    CUSTOMER_DUPLICATE_PHONE(2003, "Phone number already exists"),
    CUSTOMER_DUPLICATE_EMAIL(2004, "Email already exists"),

    // ===== POS / Transaction (3000-3999) =====
    POS_INVALID_AMOUNT(3001, "Transaction amount must be > 0"),
    POS_DUPLICATE_TRANSACTION(3002, "Duplicate transaction code"),
    TRANSACTION_NOT_FOUND(3003, "Transaction not found"),

    // ===== Loyalty (4000-4999) =====
    LOYALTY_ACCOUNT_NOT_FOUND(4001, "Loyalty account not found"),
    LOYALTY_INSUFFICIENT_POINTS(4002, "Insufficient loyalty points"),
    LOYALTY_INVALID_POINTS(4003, "Points must be greater than 0"),
    LOYALTY_TIER_NOT_FOUND(4004, "Loyalty tier not found"),
    LOYALTY_TIER_INVALID(4005, "Invalid loyalty tier");

    private final int code;
    private final String defaultMessage;

    BusinessErrorCode(int code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    public int getCode() {
        return code;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    /**
     * Trả về chuỗi mã lỗi dạng "SERVICE_NUMBER".
     * VD: LOYALTY_4002
     */
    public String getFormattedCode() {
        String prefix = switch (this) {
            case COMMON_VALIDATION_ERROR, COMMON_INTERNAL_ERROR, COMMON_RESOURCE_NOT_FOUND -> "COMMON";
            case CUSTOMER_NOT_FOUND, CUSTOMER_INACTIVE, CUSTOMER_DUPLICATE_PHONE, CUSTOMER_DUPLICATE_EMAIL -> "CUSTOMER";
            case POS_INVALID_AMOUNT, POS_DUPLICATE_TRANSACTION, TRANSACTION_NOT_FOUND -> "POS";
            case LOYALTY_ACCOUNT_NOT_FOUND, LOYALTY_INSUFFICIENT_POINTS, LOYALTY_INVALID_POINTS, LOYALTY_TIER_NOT_FOUND, LOYALTY_TIER_INVALID -> "LOYALTY";
        };
        return prefix + "_" + code;
    }
}