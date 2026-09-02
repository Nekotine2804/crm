package com.sofitech.hoamaimart.customer.adapter.in.web.dto;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

/**
 * Request DTO for updating customer.
 */
public record UpdateCustomerRequest(
        @Pattern(regexp = "^0[0-9]{9,10}$", message = "SĐT phải bắt đầu bằng 0, có 10-11 số")
        String phone,

        String name,

        @Past(message = "Ngày sinh phải là ngày trong quá khứ")
        LocalDate dateOfBirth
) {}
