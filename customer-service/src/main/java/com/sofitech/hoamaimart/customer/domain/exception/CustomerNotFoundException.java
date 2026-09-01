package com.sofitech.hoamaimart.customer.domain.exception;

import java.util.UUID;

/**
 * Exception khi không tìm thấy customer.
 */
public class CustomerNotFoundException extends RuntimeException {

    private final UUID customerId;

    public CustomerNotFoundException(UUID customerId) {
        super("Không tìm thấy khách hàng: " + customerId);
        this.customerId = customerId;
    }

    public UUID getCustomerId() {
        return customerId;
    }
}
