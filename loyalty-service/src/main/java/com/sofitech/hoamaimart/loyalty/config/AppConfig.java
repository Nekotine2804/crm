package com.sofitech.hoamaimart.loyalty.config;

import com.sofitech.hoamaimart.loyalty.application.service.LoyaltyService;
import com.sofitech.hoamaimart.loyalty.domain.port.in.LoyaltyCommandService;
import com.sofitech.hoamaimart.loyalty.domain.port.out.LoyaltyRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration: wire các bean theo Hexagonal architecture.
 */
@Configuration
public class AppConfig {

    @Bean
    public LoyaltyService loyaltyService(LoyaltyRepository loyaltyRepository) {
        return new LoyaltyService(loyaltyRepository);
    }

    @Bean
    public LoyaltyCommandService loyaltyCommandService(LoyaltyService loyaltyService) {
        return loyaltyService; // LoyaltyService implements LoyaltyCommandService
    }
}
