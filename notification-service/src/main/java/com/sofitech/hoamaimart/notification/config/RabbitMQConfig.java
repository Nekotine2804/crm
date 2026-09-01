package com.sofitech.hoamaimart.notification.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ configuration cho notification-service.
 */
@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "hoamai.exchange";

    // Queues
    public static final String POINTS_EARNED_QUEUE = "notification.points.earned.queue";
    public static final String TIER_UPGRADED_QUEUE = "notification.tier.upgraded.queue";
    public static final String POINTS_REDEEMED_QUEUE = "notification.points.redeemed.queue";
    public static final String REFUND_QUEUE = "notification.refund.queue";

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue pointsEarnedQueue() {
        return QueueBuilder.durable(POINTS_EARNED_QUEUE).build();
    }

    @Bean
    public Queue tierUpgradedQueue() {
        return QueueBuilder.durable(TIER_UPGRADED_QUEUE).build();
    }

    @Bean
    public Queue pointsRedeemedQueue() {
        return QueueBuilder.durable(POINTS_REDEEMED_QUEUE).build();
    }

    @Bean
    public Queue refundQueue() {
        return QueueBuilder.durable(REFUND_QUEUE).build();
    }

    @Bean
    public Binding pointsEarnedBinding(Queue pointsEarnedQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(pointsEarnedQueue).to(topicExchange).with("loyalty.points.earned");
    }

    @Bean
    public Binding tierUpgradedBinding(Queue tierUpgradedQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(tierUpgradedQueue).to(topicExchange).with("loyalty.tier.upgraded");
    }

    @Bean
    public Binding pointsRedeemedBinding(Queue pointsRedeemedQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(pointsRedeemedQueue).to(topicExchange).with("loyalty.points.redeemed");
    }

    @Bean
    public Binding refundBinding(Queue refundQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(refundQueue).to(topicExchange).with("transaction.refunded");
    }

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
