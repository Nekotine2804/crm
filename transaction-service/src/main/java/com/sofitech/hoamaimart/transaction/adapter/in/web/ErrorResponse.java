package com.sofitech.hoamaimart.transaction.adapter.in.web;

/**
 * Error response DTO.
 */
public record ErrorResponse(
        String error,
        String message,
        int status
) {}