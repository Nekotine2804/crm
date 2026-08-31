package com.sofitech.hoamaimart.transaction.adapter.in.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Request DTO cho API tạo giao dịch.
 */
public record CreateTransactionRequest(
        @NotNull(message = "Mã khách hàng không được để trống")
        UUID customerId,

        @NotBlank(message = "Mã cửa hàng không được để trống")
        String storeId,

        @NotBlank(message = "Mã giao dịch (transactionCode) không được để trống - dùng cho idempotency")
        String transactionCode,

        @NotNull(message = "Số tiền không được để trống")
        @DecimalMin(value = "0.01", message = "Số tiền phải lớn hơn 0")
        BigDecimal amount
) {}