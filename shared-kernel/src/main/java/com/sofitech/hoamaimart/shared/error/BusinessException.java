package com.sofitech.hoamaimart.shared.error;


public class BusinessException extends RuntimeException {

    private final BusinessErrorCode errorCode;

    public BusinessException(BusinessErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
    }

    public BusinessException(BusinessErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
    }

    public BusinessErrorCode getErrorCode() {
        return errorCode;
    }

    public static BusinessException of(BusinessErrorCode errorCode) {
        return new BusinessException(errorCode);
    }

    public static BusinessException of(BusinessErrorCode errorCode, String customMessage) {
        return new BusinessException(errorCode, customMessage);
    }
}