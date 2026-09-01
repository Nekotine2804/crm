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
import java.nio.charset.StandardCharsets;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service: xử lý use case loyalty.
 * Convert IllegalArgumentException (domain) → BusinessException (gắn error code).
 */
public class LoyaltyService implements LoyaltyCommandService {

    private final LoyaltyRepository loyaltyRepository;

    public LoyaltyService(LoyaltyRepository loyaltyRepository) {
        this.loyaltyRepository = loyaltyRepository;
    }

    /** Creates the initial account once; safe when an event is redelivered. */
    @Transactional
    public LoyaltyAccount ensureAccount(UUID customerId) {
        return loyaltyRepository.findByCustomerId(customerId)
                .orElseGet(() -> loyaltyRepository.save(LoyaltyAccount.createNew(customerId)));
    }

    @Override
    public void addPoints(UUID customerId, BigDecimal transactionAmount) {
        // 1. Tìm hoặc tạo loyalty account
        LoyaltyAccount account = loyaltyRepository.findByCustomerId(customerId)
                .orElseGet(() -> LoyaltyAccount.createNew(customerId));

        // 2. Tính điểm earned
        Points earnedPoints = Points.fromAmount(transactionAmount);
        
        // 3. Cập nhật points trong account
        if (!earnedPoints.isZero()) {
            account.addPointsFromTransaction(transactionAmount);
        }

        // 4. Ghi lịch sử chi tiêu cho rolling window
        UUID transactionId = UUID.randomUUID();
        loyaltyRepository.recordSpending(
                customerId,
                transactionId,
                transactionAmount,
                Instant.now()
        );

        // 5. Đánh giá tier
        BigDecimal rollingSpending = loyaltyRepository.calculateRollingWindowSpending(customerId);
        account.evaluateTier(rollingSpending.longValue());

        // 6. Lưu account
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
     * @return PointTransaction đã tạo, hoặc null nếu đã tồn tại
     */
    public PointTransaction earnPoints(
            UUID customerId,
            String transactionReference,
            BigDecimal amount
    ) {
        UUID transactionId = UUID.nameUUIDFromBytes(
                transactionReference.getBytes(StandardCharsets.UTF_8));
        return earnPoints(customerId, transactionId, transactionReference, amount);
    }

    @Transactional
    public PointTransaction earnPoints(
            UUID customerId,
            UUID transactionId,
            String transactionReference,
            BigDecimal amount
    ) {
        // 1. Idempotency check
        if (loyaltyRepository.existsPointTransactionByReferenceIdAndType(
                transactionReference, PointTransactionType.EARN)) {
            return null; // Đã tích điểm cho transaction này rồi
        }

        // 2. Tìm hoặc tạo loyalty account
        LoyaltyAccount account = loyaltyRepository.findByCustomerId(customerId)
                .orElseGet(() -> LoyaltyAccount.createNew(customerId));

        // 3. Tính điểm earned
        Points earnedPoints = Points.fromAmount(amount);
        
        // 4. Cập nhật points trong account
        if (!earnedPoints.isZero()) {
            account.addPointsFromTransaction(amount);
        }

        // 5. Ghi lịch sử chi tiêu cho rolling window
        loyaltyRepository.recordSpending(
                customerId,
                transactionId,
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
                transactionReference
        );

        return loyaltyRepository.savePointTransaction(pointTransaction);
    }

    /**
     * Ghi nhận đổi điểm.
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

    /**
     * Hoàn điểm loyalty khi transaction được refund.
     */
    public PointTransaction refundPoints(
            UUID customerId,
            String originalTransactionCode,
            BigDecimal refundAmount
    ) {
        // 1. Kiểm tra đã có refund cho transaction này chưa
        if (loyaltyRepository.existsPointTransactionByReferenceIdAndType(
                originalTransactionCode, PointTransactionType.REFUND)) {
            return null; // Đã refund rồi
        }

        // 2. Tìm tài khoản
        LoyaltyAccount account = loyaltyRepository.findByCustomerId(customerId)
                .orElse(null); // Khách không có tài khoản loyalty thì không cần refund

        if (account == null) {
            return null;
        }

        // 3. Tính điểm đã tích từ transaction này
        Points refundPoints = Points.fromAmount(refundAmount);

        // 4. Trừ điểm (vì refund = hoàn tiền = không được tích điểm)
        try {
            account.redeem(refundPoints);
        } catch (IllegalArgumentException e) {
            // Không đủ điểm để trừ - có thể đã đổi hết rồi
            // Vẫn ghi nhận refund transaction
        }

        // 5. Lưu account
        LoyaltyAccount savedAccount = loyaltyRepository.save(account);

        // 6. Tạo và lưu refund point transaction
        PointTransaction pointTransaction = PointTransaction.refund(
                savedAccount.getId(),
                customerId,
                refundPoints.value(),
                savedAccount.getPoints().value(),
                originalTransactionCode
        );

        return loyaltyRepository.savePointTransaction(pointTransaction);
    }
}
