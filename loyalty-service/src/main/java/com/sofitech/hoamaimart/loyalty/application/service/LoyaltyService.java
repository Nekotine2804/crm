package com.sofitech.hoamaimart.loyalty.application.service;

import com.sofitech.hoamaimart.loyalty.adapter.out.messaging.LoyaltyEventPublisher;
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
 */
public class LoyaltyService implements LoyaltyCommandService {

    private final LoyaltyRepository loyaltyRepository;
    private final LoyaltyEventPublisher eventPublisher;

    public LoyaltyService(LoyaltyRepository loyaltyRepository, LoyaltyEventPublisher eventPublisher) {
        this.loyaltyRepository = loyaltyRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public LoyaltyAccount ensureAccount(UUID customerId) {
        return loyaltyRepository.findByCustomerId(customerId)
                .orElseGet(() -> loyaltyRepository.save(LoyaltyAccount.createNew(customerId)));
    }

    @Override
    public void addPoints(UUID customerId, BigDecimal transactionAmount) {
        LoyaltyAccount account = loyaltyRepository.findByCustomerId(customerId)
                .orElseGet(() -> LoyaltyAccount.createNew(customerId));

        Points earnedPoints = Points.fromAmount(transactionAmount);

        if (!earnedPoints.isZero()) {
            account.addPointsFromTransaction(transactionAmount);
        }

        UUID transactionId = UUID.randomUUID();
        loyaltyRepository.recordSpending(
                customerId, transactionId, transactionAmount, Instant.now()
        );

        BigDecimal rollingSpending = loyaltyRepository.calculateRollingWindowSpending(customerId);
        account.evaluateTier(rollingSpending.longValue());

        loyaltyRepository.save(account);
    }

    @Override
    public LoyaltyAccount redeem(UUID customerId, int pointsToRedeem) {
        if (pointsToRedeem <= 0) {
            throw BusinessException.of(
                BusinessErrorCode.LOYALTY_INVALID_POINTS,
                "Số điểm quy đổi phải > 0, nhận: " + pointsToRedeem
            );
        }

        LoyaltyAccount account = loyaltyRepository.findByCustomerId(customerId)
                .orElseThrow(() -> BusinessException.of(
                    BusinessErrorCode.LOYALTY_ACCOUNT_NOT_FOUND,
                    "Khách hàng chưa có tài khoản loyalty: " + customerId
                ));

        try {
            account.redeem(Points.of(pointsToRedeem));
        } catch (IllegalArgumentException e) {
            throw BusinessException.of(BusinessErrorCode.LOYALTY_INSUFFICIENT_POINTS, e.getMessage());
        }

        LoyaltyAccount savedAccount = loyaltyRepository.save(account);

        // Publish event
        eventPublisher.publishPointsRedeemed(customerId, pointsToRedeem, "Reward", savedAccount.getPoints().value());

        return savedAccount;
    }

    public PointTransaction earnPoints(
            UUID customerId,
            String transactionReference,
            BigDecimal amount
    ) {
        UUID transactionId = UUID.nameUUIDFromBytes(transactionReference.getBytes(StandardCharsets.UTF_8));
        return earnPoints(customerId, transactionId, transactionReference, amount);
    }

    @Transactional
    public PointTransaction earnPoints(
            UUID customerId,
            UUID transactionId,
            String transactionReference,
            BigDecimal amount
    ) {
        // Idempotency check
        if (loyaltyRepository.existsPointTransactionByReferenceIdAndType(
                transactionReference, PointTransactionType.EARN)) {
            return null;
        }

        LoyaltyAccount account = loyaltyRepository.findByCustomerId(customerId)
                .orElseGet(() -> LoyaltyAccount.createNew(customerId));

        Points earnedPoints = Points.fromAmount(amount);
        String oldTier = account.getTier().name();

        if (!earnedPoints.isZero()) {
            account.addPointsFromTransaction(amount);
        }

        loyaltyRepository.recordSpending(customerId, transactionId, amount, Instant.now());

        BigDecimal rollingSpending = loyaltyRepository.calculateRollingWindowSpending(customerId);
        account.evaluateTier(rollingSpending.longValue());

        LoyaltyAccount savedAccount = loyaltyRepository.save(account);

        int newBalance = savedAccount.getPoints().value();
        PointTransaction pointTransaction = PointTransaction.earn(
                savedAccount.getId(), customerId, earnedPoints.value(), newBalance, transactionReference
        );

        PointTransaction saved = loyaltyRepository.savePointTransaction(pointTransaction);

        // Publish events
        eventPublisher.publishPointsEarned(customerId, transactionReference, earnedPoints.value(), newBalance, savedAccount.getTier().name());

        // Check tier upgrade
        if (!oldTier.equals(savedAccount.getTier().name())) {
            eventPublisher.publishTierUpgraded(customerId, oldTier, savedAccount.getTier().name());
        }

        return saved;
    }

    public PointTransaction recordRedeem(
            UUID customerId,
            int pointsToRedeem,
            String redemptionId
    ) {
        LoyaltyAccount account = loyaltyRepository.findByCustomerId(customerId)
                .orElseThrow(() -> BusinessException.of(
                    BusinessErrorCode.LOYALTY_ACCOUNT_NOT_FOUND,
                    "Khách hàng chưa có tài khoản loyalty: " + customerId
                ));

        try {
            account.redeem(Points.of(pointsToRedeem));
        } catch (IllegalArgumentException e) {
            throw BusinessException.of(BusinessErrorCode.LOYALTY_INSUFFICIENT_POINTS, e.getMessage());
        }

        LoyaltyAccount savedAccount = loyaltyRepository.save(account);

        PointTransaction pointTransaction = PointTransaction.redeem(
                savedAccount.getId(), customerId, pointsToRedeem,
                savedAccount.getPoints().value(), redemptionId
        );

        return loyaltyRepository.savePointTransaction(pointTransaction);
    }

    public PointTransaction refundPoints(
            UUID customerId,
            String originalTransactionCode,
            BigDecimal refundAmount
    ) {
        if (loyaltyRepository.existsPointTransactionByReferenceIdAndType(
                originalTransactionCode, PointTransactionType.REFUND)) {
            return null;
        }

        LoyaltyAccount account = loyaltyRepository.findByCustomerId(customerId).orElse(null);
        if (account == null) {
            return null;
        }

        Points refundPoints = Points.fromAmount(refundAmount);

        try {
            account.redeem(refundPoints);
        } catch (IllegalArgumentException e) {
            // Ignore if not enough points
        }

        LoyaltyAccount savedAccount = loyaltyRepository.save(account);

        PointTransaction pointTransaction = PointTransaction.refund(
                savedAccount.getId(), customerId, refundPoints.value(),
                savedAccount.getPoints().value(), originalTransactionCode
        );

        return loyaltyRepository.savePointTransaction(pointTransaction);
    }
}
