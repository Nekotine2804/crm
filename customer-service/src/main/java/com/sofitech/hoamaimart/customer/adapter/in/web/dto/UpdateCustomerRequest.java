package com.sofitech.hoamaimart.customer.adapter.in.web.dto;

import jakarta.validation.constraints.Pattern;

/**
 * Request DTO for updating customer.
 */
public record UpdateCustomerRequest(
        @Pattern(regexp = "^0[0-9]{9,10}$", message = "SĐT phải bắt đầu bằng 0, có 10-11 số")
        String phone,
        
        String name
) {}
