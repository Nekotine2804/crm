package com.sofitech.hoamaimart.notification.config;

import com.sofitech.hoamaimart.notification.adapter.out.persistence.repository.NotificationJpaRepository;
import com.sofitech.hoamaimart.notification.adapter.out.persistence.repository.NotificationRepositoryAdapter;
import com.sofitech.hoamaimart.notification.application.service.NotificationService;
import com.sofitech.hoamaimart.notification.domain.port.out.NotificationRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Application configuration.
 */
@Configuration
public class AppConfig {

    @Bean
    public NotificationRepository notificationRepository(NotificationJpaRepository jpaRepository) {
        return new NotificationRepositoryAdapter(jpaRepository);
    }

    @Bean
    public NotificationService notificationService(NotificationRepository notificationRepository) {
        return new NotificationService(notificationRepository);
    }
}
