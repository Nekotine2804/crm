package com.sofitech.hoamaimart.loyalty.application.service;

import com.sofitech.hoamaimart.loyalty.domain.model.LoyaltyAccount;
import com.sofitech.hoamaimart.loyalty.domain.model.Points;
import com.sofitech.hoamaimart.loyalty.domain.model.PointTransaction;
import com.sofitech.hoamaimart.loyalty.domain.model.PointTransactionType;
import com.sofitech.hoamaimart.loyalty.domain.port.in.LoyaltyCommandService;
import com.sofitech.hoamaimart.loyalty.domain.port.out.LoyaltyRepository;
import com.sofitech.hoamaimart.shared.error.BusinessErrorCode;
import com.sofitech.hoamaimart.shared.error.BusinessException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Application service: xử lý use case loyalty.
 * Convert IllegalArgumentException (domain) → BusinessException (gắn error code).
 */
public class LoyaltyService implements LoyaltyCommandService {

    private final LoyaltyRepository loyaltyRepository;

    public LoyaltyService(LoyaltyRepository loyaltyRepository) {
        this.loyaltyRepository = loyaltyRepository;
    }

    @Override
    public void addPoints(UUID customerId, BigDecimal transactionAmount) {
        // 1. Idempotency check - kiểm tra đã tích điểm cho transaction này chưa
        // (Được gọi từ event listener, nên cần check)
        
        // 2. Tìm hoặc tạo loyalty account
        LoyaltyAccount account = loyaltyRepository.findByCustomerId(customerId)
                .orElseGet(() -> LoyaltyAccount.createNew(customerId));

        // 3. Tính điểm earned
        Points earnedPoints = Points.fromAmount(transactionAmount);
        
        // 4. Cập nhật points trong account
        if (!earnedPoints.isZero()) {
            account.addPointsFromTransaction(transactionAmount);
        }

        // 5. Ghi lịch sử chi tiêu cho rolling window
        UUID transactionId = UUID.randomUUID(); // TODO: nhận từ event
        loyaltyRepository.recordSpending(
                customerId,
                transactionId,
                transactionAmount,
                Instant.now()
        );

        // 6. Đánh giá tier
        BigDecimal rollingSpending = loyaltyRepository.calculateRollingWindowSpending(customerId);
        account.evaluateTier(rollingSpending.longValue());

        // 7. Lưu account
        loyaltyRepository.save(account);
    }

    @Override
    public LoyaltyAccount redeem(UUID customerId, int pointsToRedeem) {
        // 1. Validate input
        if (pointsToRedeem <= 0) {
            throw BusinessException.of(
                BusinessErrorCode.LOYALTY_INVALID_POINTS,
                "Số điểm quy đổi phải > 0, nhận: " + pointsToRedeem
            );
        }

        // 2. Tìm tài khoản
        LoyaltyAccount account = loyaltyRepository.findByCustomerId(customerId)
                .orElseThrow(() -> BusinessException.of(
                    BusinessErrorCode.LOYALTY_ACCOUNT_NOT_FOUND,
                    "Khách hàng chưa có tài khoản loyalty: " + customerId
                ));

        // 3. Trừ điểm (domain throw IllegalArgumentException)
        try {
            account.redeem(Points.of(pointsToRedeem));
        } catch (IllegalArgumentException e) {
            throw BusinessException.of(BusinessErrorCode.LOYALTY_INSUFFICIENT_POINTS, e.getMessage());
        }

        // 4. Lưu account
        return loyaltyRepository.save(account);
    }

    /**
     * Ghi nhận tích điểm từ transaction (có idempotency check).
     * @return PointTransaction đã tạo, hoặc empty nếu đã tồn tại
     */
    public PointTransaction earnPoints(
            UUID customerId, 
            String transactionId, 
            BigDecimal amount
    ) {
        // 1. Idempotency check
        if (loyaltyRepository.existsPointTransactionByReferenceIdAndType(
                transactionId, PointTransactionType.EARN)) {
            // Đã tích điểm cho transaction này rồi
            return null;
        }

        // 2. Tìm hoặc tạo loyalty account
        LoyaltyAccount account = loyaltyRepository.findByCustomerId(customerId)
                .orElseGet(() -> LoyaltyAccount.createNew(customerId));

        // 3. Tính điểm earned
        Points earnedPoints = Points.fromAmount(amount);
        
        int oldBalance = account.getPoints().value();
        
        // 4. Cập nhật points trong account
        if (!earnedPoints.isZero()) {
            account.addPointsFromTransaction(amount);
        }

        // 5. Ghi lịch sử chi tiêu cho rolling window
        loyaltyRepository.recordSpending(
                customerId,
                UUID.fromString(transactionId),
                amount,
                Instant.now()
        );

        // 6. Đánh giá tier
        BigDecimal rollingSpending = loyaltyRepository.calculateRollingWindowSpending(customerId);
        account.evaluateTier(rollingSpending.longValue());

        // 7. Lưu account
        LoyaltyAccount savedAccount = loyaltyRepository.save(account);

        // 8. Tạo và lưu point transaction
        int newBalance = savedAccount.getPoints().value();
        PointTransaction pointTransaction = PointTransaction.earn(
                savedAccount.getId(),
                customerId,
                earnedPoints.value(),
                newBalance,
                transactionId
        );

        return loyaltyRepository.savePointTransaction(pointTransaction);
    }

    /**
     * Ghi nhận đổi điểm.
     * @return PointTransaction đã tạo
     */
    public PointTransaction recordRedeem(
            UUID customerId,
            int pointsToRedeem,
            String redemptionId
    ) {
        // 1. Tìm tài khoản
        LoyaltyAccount account = loyaltyRepository.findByCustomerId(customerId)
                .orElseThrow(() -> BusinessException.of(
                    BusinessErrorCode.LOYALTY_ACCOUNT_NOT_FOUND,
                    "Khách hàng chưa có tài khoản loyalty: " + customerId
                ));

        // 2. Trừ điểm
        try {
            account.redeem(Points.of(pointsToRedeem));
        } catch (IllegalArgumentException e) {
            throw BusinessException.of(BusinessErrorCode.LOYALTY_INSUFFICIENT_POINTS, e.getMessage());
        }

        // 3. Lưu account
        LoyaltyAccount savedAccount = loyaltyRepository.save(account);

        // 4. Tạo và lưu point transaction
        PointTransaction pointTransaction = PointTransaction.redeem(
                savedAccount.getId(),
                customerId,
                pointsToRedeem,
                savedAccount.getPoints().value(),
                redemptionId
        );

        return loyaltyRepository.savePointTransaction(pointTransaction);
    }
}
