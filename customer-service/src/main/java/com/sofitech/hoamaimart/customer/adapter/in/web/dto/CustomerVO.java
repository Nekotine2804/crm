package com.sofitech.hoamaimart.customer.adapter.in.web.dto;

import com.sofitech.hoamaimart.customer.domain.model.Customer;

import java.time.Instant;
import java.util.UUID;

/**
 * View Object - trả về cho client khi query customer.
 */
public record CustomerVO(
        UUID customerId,
        String phone,
        String formattedPhone,  // VO: PhoneNumber.format()
        String name,
        Instant createdAt,
        Instant updatedAt
) {

    public static CustomerVO from(Customer customer) {
        return new CustomerVO(
                customer.getId(),
                customer.getPhoneValue(),
                customer.getPhone().format(),
                customer.getNameValue(),
                customer.getCreatedAt(),
                customer.getUpdatedAt()
        );
    }
}