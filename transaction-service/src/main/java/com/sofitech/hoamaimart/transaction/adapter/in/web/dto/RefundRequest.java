package com.sofitech.hoamaimart.transaction.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for refunding a transaction.
 */
public record RefundRequest(
        @NotBlank(message = "Refund reason is required")
        String reason
) {}
