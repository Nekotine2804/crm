package com.sofitech.hoamaimart.loyalty.adapter.in.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Request DTO for earning points.
 */
public record EarnPointsRequest(
        @NotBlank(message = "Transaction code is required")
        String transactionCode,
        
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
        BigDecimal amount
) {}
