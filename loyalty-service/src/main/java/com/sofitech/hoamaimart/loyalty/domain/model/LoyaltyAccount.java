package com.sofitech.hoamaimart.loyalty.domain.model;

import java.time.Instant;
import java.util.UUID;

/**
 * Loyalty Account - aggregate root.
 * Theo dõi điểm tích lũy và hạng thành viên của khách hàng.
 */
public class LoyaltyAccount {

    private final UUID id;
    private final UUID customerId;
    private Points points;       // Tổng điểm tích lũy (dùng cho khuyến mãi)
    private Tier tier;           // Hạng hiện tại (dựa trên rolling window)
    private Tier pendingTier;    // Hạng đang chờ lên (khi đủ điều kiện)
    private Instant lastTierEvaluation;
    private final Instant createdAt;
    private Instant updatedAt;

    public LoyaltyAccount(UUID id, UUID customerId, Points points, Tier tier,
                         Tier pendingTier, Instant lastTierEvaluation,
                         Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.customerId = customerId;
        this.points = points;
        this.tier = tier;
        this.pendingTier = pendingTier;
        this.lastTierEvaluation = lastTierEvaluation;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Factory: tạo tài khoản loyalty mới cho khách.
     */
    public static LoyaltyAccount createNew(UUID customerId) {
        Instant now = Instant.now();
        return new LoyaltyAccount(
                UUID.randomUUID(),
                customerId,
                Points.of(0),
                Tier.BRONZE,
                Tier.BRONZE,
                now,
                now,
                now
        );
    }

    /**
     * Cộng điểm từ giao dịch.
     * 10.000 VND = 1 điểm
     */
    public void addPointsFromTransaction(java.math.BigDecimal amount) {
        Points earnedPoints = Points.fromAmount(amount);
        if (!earnedPoints.isZero()) {
            this.points = this.points.add(earnedPoints);
            this.updatedAt = Instant.now();
        }
    }

    /**
     * Quy đổi điểm (redeem).
     * @throws IllegalArgumentException nếu không đủ điểm hoặc points không hợp lệ
     */
    public void redeem(Points pointsToRedeem) {
        if (pointsToRedeem == null || pointsToRedeem.isZero()) {
            throw new IllegalArgumentException("Số điểm quy đổi phải > 0");
        }
        if (!this.points.isGreaterThanOrEqual(pointsToRedeem)) {
            throw new IllegalArgumentException(
                "Không đủ điểm để quy đổi. Hiện có: " + this.points.value()
                + ", yêu cầu: " + pointsToRedeem.value()
            );
        }
        this.points = this.points.subtract(pointsToRedeem);
        this.updatedAt = Instant.now();
    }

    /**
     * Cập nhật tier dựa trên chi tiêu rolling window.
     * Gọi định kỳ (hàng tháng/quý).
     */
    public void evaluateTier(long rollingWindowSpendingVnd) {
        Tier newTier = Tier.fromSpending(rollingWindowSpendingVnd);

        // Nếu chi tiêu đủ điều kiện lên tier cao hơn
        if (newTier.ordinal() > this.tier.ordinal()) {
            this.pendingTier = newTier;
        }

        // Nếu chi tiêu giảm → rớt hạng
        if (newTier.ordinal() < this.tier.ordinal()) {
            this.tier = newTier;
            this.pendingTier = newTier;
        }

        this.lastTierEvaluation = Instant.now();
        this.updatedAt = Instant.now();
    }

    /**
     * Xác nhận lên tier mới (sau thời gian thử thách).
     */
    public void confirmTierUpgrade() {
        if (this.pendingTier != null && this.pendingTier.ordinal() > this.tier.ordinal()) {
            this.tier = this.pendingTier;
            this.pendingTier = this.tier;
        }
    }

    /**
     * Kiểm tra tier có đang bị treo không (chưa mua quá 90 ngày).
     */
    public boolean isDormant(Instant lastTransactionAt) {
        return lastTransactionAt.plusSeconds(90 * 24 * 60 * 60).isBefore(Instant.now());
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getCustomerId() { return customerId; }
    public Points getPoints() { return points; }
    public Tier getTier() { return tier; }
    public Tier getPendingTier() { return pendingTier; }
    public Instant getLastTierEvaluation() { return lastTierEvaluation; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}