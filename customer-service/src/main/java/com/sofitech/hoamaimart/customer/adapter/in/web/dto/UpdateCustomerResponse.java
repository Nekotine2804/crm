package com.sofitech.hoamaimart.customer.adapter.in.web.dto;

import com.sofitech.hoamaimart.customer.domain.model.Customer;

import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO for updated customer.
 */
public record UpdateCustomerResponse(
        UUID id,
        String phone,
        String name,
        Instant createdAt,
        Instant updatedAt
) {
    public static UpdateCustomerResponse from(Customer customer) {
        return new UpdateCustomerResponse(
                customer.getId(),
                customer.getPhoneValue(),
                customer.getNameValue(),
                customer.getCreatedAt(),
                customer.getUpdatedAt()
        );
    }
}
