package com.sofitech.hoamaimart.transaction.domain.exception;

/**
 * Exception khi transaction ở trạng thái không hợp lệ.
 */
public class InvalidTransactionStateException extends RuntimeException {

    public InvalidTransactionStateException(String message) {
        super(message);
    }
}
