package com.sofitech.hoamaimart.transaction.config;

import com.sofitech.hoamaimart.transaction.adapter.out.messaging.RabbitMQEventPublisher;
import com.sofitech.hoamaimart.transaction.adapter.out.persistence.mapper.TransactionMapper;
import com.sofitech.hoamaimart.transaction.adapter.out.persistence.repository.TransactionJpaRepository;
import com.sofitech.hoamaimart.transaction.adapter.out.persistence.repository.TransactionRepositoryAdapter;
import com.sofitech.hoamaimart.transaction.application.service.CreateTransactionService;
import com.sofitech.hoamaimart.transaction.domain.port.in.CreateTransactionCommandService;
import com.sofitech.hoamaimart.transaction.domain.port.out.EventPublisher;
import com.sofitech.hoamaimart.transaction.domain.port.out.TransactionRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration: wire các bean theo Hexagonal architecture.
 */
@Configuration
public class AppConfig {

    @Bean
    public TransactionRepository transactionRepository(
            TransactionJpaRepository jpaRepository,
            TransactionMapper mapper
    ) {
        return new TransactionRepositoryAdapter(jpaRepository, mapper);
    }

    @Bean
    public EventPublisher eventPublisher(
            RabbitTemplate rabbitTemplate,
            @Value("${app.rabbitmq.exchange:hoamai.exchange}") String exchange
    ) {
        return new RabbitMQEventPublisher(rabbitTemplate, exchange);
    }

    @Bean
    public CreateTransactionCommandService createTransactionService(
            TransactionRepository repository,
            EventPublisher eventPublisher
    ) {
        return new CreateTransactionService(repository, eventPublisher);
    }
}