package com.sofitech.hoamaimart.customer.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Request DTO cho API tạo khách hàng mới.
 * Validation ở tầng adapter (web), không đưa vào domain.
 */
public record CreateCustomerRequest(
        @NotBlank(message = "SĐT không được để trống")
        String phone,

        @NotBlank(message = "Tên không được để trống")
        @Size(min = 2, max = 255, message = "Tên phải từ 2-255 ký tự")
        String name,

        @Past(message = "Ngày sinh phải là ngày trong quá khứ")
        LocalDate dateOfBirth
) {}