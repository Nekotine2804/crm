package com.sofitech.hoamaimart.loyalty.config;

import com.sofitech.hoamaimart.loyalty.adapter.out.messaging.LoyaltyEventPublisher;
import com.sofitech.hoamaimart.loyalty.application.service.LoyaltyService;
import com.sofitech.hoamaimart.loyalty.domain.port.in.LoyaltyCommandService;
import com.sofitech.hoamaimart.loyalty.domain.port.out.LoyaltyRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public LoyaltyService loyaltyService(LoyaltyRepository loyaltyRepository, LoyaltyEventPublisher eventPublisher) {
        return new LoyaltyService(loyaltyRepository, eventPublisher);
    }

    @Bean
    public LoyaltyCommandService loyaltyCommandService(LoyaltyService loyaltyService) {
        return loyaltyService;
    }
}
