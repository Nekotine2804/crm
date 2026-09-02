package com.sofitech.hoamaimart.customer.adapter.in.web.dto;

import com.sofitech.hoamaimart.customer.domain.model.Customer;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Response DTO sau khi tạo khách hàng thành công.
 */
public record CreateCustomerResponse(
        UUID customerId,
        String phone,
        String name,
        LocalDate dateOfBirth,
        Instant createdAt
) {

    /**
     * Factory: tạo response từ Domain entity.
     */
    public static CreateCustomerResponse from(Customer customer) {
        return new CreateCustomerResponse(
                customer.getId(),
                customer.getPhoneValue(),
                customer.getNameValue(),
                customer.getDateOfBirth(),
                customer.getCreatedAt()
        );
    }
}