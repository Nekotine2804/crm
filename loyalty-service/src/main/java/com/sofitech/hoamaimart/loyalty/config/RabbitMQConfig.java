package com.sofitech.hoamaimart.loyalty.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ configuration: exchange + queue cho loyalty-service.
 */
@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "hoamai.exchange";
    public static final String LOYALTY_TRANSACTION_QUEUE = "loyalty.transaction.queue";
    public static final String LOYALTY_REFUND_QUEUE = "loyalty.refund.queue";

    // TopicExchange
    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(EXCHANGE);
    }

    // Queue for transaction completed events
    @Bean
    public Queue loyaltyTransactionQueue() {
        return QueueBuilder.durable(LOYALTY_TRANSACTION_QUEUE).build();
    }

    // Queue for refund events
    @Bean
    public Queue loyaltyRefundQueue() {
        return QueueBuilder.durable(LOYALTY_REFUND_QUEUE).build();
    }

    // Binding: queue -> exchange với routing key
    @Bean
    public Binding loyaltyBinding(Queue loyaltyTransactionQueue, TopicExchange topicExchange) {
        return BindingBuilder
                .bind(loyaltyTransactionQueue)
                .to(topicExchange)
                .with("transaction.completed");
    }

    // Binding for refund events
    @Bean
    public Binding loyaltyRefundBinding(Queue loyaltyRefundQueue, TopicExchange topicExchange) {
        return BindingBuilder
                .bind(loyaltyRefundQueue)
                .to(topicExchange)
                .with("transaction.refunded");
    }

    // Message converter: serialize event thành JSON
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }
}
