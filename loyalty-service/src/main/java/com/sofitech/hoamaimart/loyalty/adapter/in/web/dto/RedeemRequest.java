package com.sofitech.hoamaimart.loyalty.adapter.in.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO cho API quy đổi điểm.
 */
public record RedeemRequest(
        @NotNull(message = "Số điểm quy đổi không được null")
        @Min(value = 1, message = "Số điểm quy đổi phải >= 1")
        Integer points
) {}