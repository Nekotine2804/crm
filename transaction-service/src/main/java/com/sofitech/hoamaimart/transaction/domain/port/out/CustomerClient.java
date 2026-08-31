package com.sofitech.hoamaimart.transaction.domain.port.out;

import java.util.UUID;

/**
 * Port OUT: gọi sang customer-service để validate khách hàng.
 * Giả lập - production sẽ dùng REST Client / OpenFeign.
 */
public interface CustomerClient {

    /**
     * Kiểm tra khách hàng có tồn tại và ACTIVE không.
     * @throws com.sofitech.hoamaimart.shared.error.BusinessException nếu không hợp lệ
     */
    void validateActiveCustomer(UUID customerId);
}