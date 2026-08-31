package com.sofitech.hoamaimart.loyalty.domain.model;

/**
 * Hạng thành viên loyalty.
 * Tier dựa trên tổng chi tiêu trong 12 tháng gần nhất (rolling window).
 */
public enum Tier {
    BRONZE,    // Mặc định
    SILVER,    // ≥ 500.000 VNĐ / 12 tháng (≥ 50 điểm = 50 giao dịch ~mua 1 tuần)
    GOLD,      // ≥ 2.000.000 VNĐ / 12 tháng (≥ 200 điểm)
    PLATINUM;  // ≥ 10.000.000 VNĐ / 12 tháng (≥ 1000 điểm)

    // Thresholds (tổng chi tiêu trong 12 tháng)
    private static final long SILVER_SPENDING = 500_000;
    private static final long GOLD_SPENDING = 2_000_000;
    private static final long PLATINUM_SPENDING = 10_000_000;

    /**
     * Tính tier dựa trên tổng chi tiêu 12 tháng gần nhất.
     */
    public static Tier fromSpending(long totalSpendingVnd) {
        if (totalSpendingVnd >= PLATINUM_SPENDING) return PLATINUM;
        if (totalSpendingVnd >= GOLD_SPENDING) return GOLD;
        if (totalSpendingVnd >= SILVER_SPENDING) return SILVER;
        return BRONZE;
    }
}