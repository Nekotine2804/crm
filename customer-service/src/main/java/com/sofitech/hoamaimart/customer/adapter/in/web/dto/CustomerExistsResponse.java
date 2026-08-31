package com.sofitech.hoamaimart.customer.adapter.in.web.dto;

/**
 * Response: kiểm tra khách hàng có tồn tại không.
 */
public record CustomerExistsResponse(
        String phone,
        boolean exists,
        java.util.UUID customerId  // null nếu không tồn tại
) {
    public static CustomerExistsResponse notFound(String phone) {
        return new CustomerExistsResponse(phone, false, null);
    }

    public static CustomerExistsResponse found(String phone, java.util.UUID customerId) {
        return new CustomerExistsResponse(phone, true, customerId);
    }
}