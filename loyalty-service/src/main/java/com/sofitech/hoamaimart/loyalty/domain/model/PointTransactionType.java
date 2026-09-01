package com.sofitech.hoamaimart.loyalty.domain.model;

/**
 * Loại giao dịch điểm.
 */
public enum PointTransactionType {
    EARN,       // Tích điểm từ mua hàng
    REDEEM,     // Đổi điểm
    ADJUST,     // Điều chỉnh thủ công (admin)
    EXPIRE,     // Điểm hết hạn
    REFUND      // Hoàn điểm (khi refund transaction)
}
