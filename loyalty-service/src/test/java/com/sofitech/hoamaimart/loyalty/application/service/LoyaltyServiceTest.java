package com.sofitech.hoamaimart.loyalty.application.service;

import com.sofitech.hoamaimart.loyalty.adapter.out.messaging.LoyaltyEventPublisher;
import com.sofitech.hoamaimart.loyalty.domain.model.LoyaltyAccount;
import com.sofitech.hoamaimart.loyalty.domain.model.PointTransaction;
import com.sofitech.hoamaimart.loyalty.domain.port.out.LoyaltyRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LoyaltyServiceTest {

    @Test
    void redeemDeductsPointsOnceAndRecordsHistory() {
        LoyaltyRepository repository = mock(LoyaltyRepository.class);
        LoyaltyEventPublisher eventPublisher = mock(LoyaltyEventPublisher.class);
        LoyaltyService service = new LoyaltyService(repository, eventPublisher);
        UUID customerId = UUID.randomUUID();
        String redemptionId = "RED-test";
        LoyaltyAccount account = LoyaltyAccount.createNew(customerId);
        account.addPointsFromTransaction(new BigDecimal("10000000"));

        when(repository.findByCustomerId(customerId)).thenReturn(Optional.of(account));
        when(repository.save(any(LoyaltyAccount.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(repository.savePointTransaction(any(PointTransaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        LoyaltyAccount redeemed = service.redeem(customerId, 1000, redemptionId);

        assertEquals(0, redeemed.getPoints().value());
        verify(repository, times(1)).save(any(LoyaltyAccount.class));
        verify(repository, times(1)).savePointTransaction(any(PointTransaction.class));
        verify(eventPublisher).publishPointsRedeemed(customerId, redemptionId, 1000, "Reward", 0);
    }
}
