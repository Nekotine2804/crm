package com.sofitech.hoamaimart.customer.adapter.in.web.dto;

/**
 * Error response DTO.
 */
public record ErrorResponse(
        String error,
        String message,
        int status
) {}