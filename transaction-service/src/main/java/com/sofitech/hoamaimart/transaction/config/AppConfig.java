package com.sofitech.hoamaimart.transaction.config;

import com.sofitech.hoamaimart.transaction.adapter.out.client.HttpCustomerClient;
import com.sofitech.hoamaimart.transaction.adapter.out.messaging.RabbitMQEventPublisher;
import com.sofitech.hoamaimart.transaction.adapter.out.persistence.repository.TransactionJpaRepository;
import com.sofitech.hoamaimart.transaction.adapter.out.persistence.repository.TransactionRepositoryAdapter;
import com.sofitech.hoamaimart.transaction.application.service.CreateTransactionService;
import com.sofitech.hoamaimart.transaction.application.service.RefundTransactionServiceImpl;
import com.sofitech.hoamaimart.transaction.domain.port.in.CreateTransactionCommandService;
import com.sofitech.hoamaimart.transaction.domain.port.in.RefundTransactionService;
import com.sofitech.hoamaimart.transaction.domain.port.out.CustomerClient;
import com.sofitech.hoamaimart.transaction.domain.port.out.EventPublisher;
import com.sofitech.hoamaimart.transaction.domain.port.out.TransactionRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    public TransactionRepository transactionRepository(TransactionJpaRepository jpaRepository) {
        return new TransactionRepositoryAdapter(jpaRepository);
    }

    @Bean
    public EventPublisher eventPublisher(
            RabbitTemplate rabbitTemplate,
            @Value("${app.rabbitmq.exchange:hoamai.exchange}") String exchange
    ) {
        return new RabbitMQEventPublisher(rabbitTemplate, exchange);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public CreateTransactionCommandService createTransactionService(
            TransactionRepository repository,
            EventPublisher eventPublisher,
            CustomerClient customerClient
    ) {
        return new CreateTransactionService(repository, eventPublisher, customerClient);
    }

    @Bean
    public RefundTransactionService refundTransactionService(
            TransactionRepository transactionRepository,
            EventPublisher eventPublisher
    ) {
        return new RefundTransactionServiceImpl(transactionRepository, eventPublisher);
    }
}
