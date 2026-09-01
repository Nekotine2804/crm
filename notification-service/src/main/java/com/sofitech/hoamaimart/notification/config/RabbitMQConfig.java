package com.sofitech.hoamaimart.notification.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "hoamai.exchange";

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue pointsEarnedQueue() {
        return QueueBuilder.durable("notification.points.earned.queue").build();
    }

    @Bean
    public Queue tierUpgradedQueue() {
        return QueueBuilder.durable("notification.tier.upgraded.queue").build();
    }

    @Bean
    public Queue pointsRedeemedQueue() {
        return QueueBuilder.durable("notification.points.redeemed.queue").build();
    }

    @Bean
    public Queue refundQueue() {
        return QueueBuilder.durable("notification.refund.queue").build();
    }

    @Bean
    public Binding pointsEarnedBinding() {
        return BindingBuilder.bind(pointsEarnedQueue()).to(topicExchange()).with("loyalty.points.earned");
    }

    @Bean
    public Binding tierUpgradedBinding() {
        return BindingBuilder.bind(tierUpgradedQueue()).to(topicExchange()).with("loyalty.tier.upgraded");
    }

    @Bean
    public Binding pointsRedeemedBinding() {
        return BindingBuilder.bind(pointsRedeemedQueue()).to(topicExchange()).with("loyalty.points.redeemed");
    }

    @Bean
    public Binding refundBinding() {
        return BindingBuilder.bind(refundQueue()).to(topicExchange()).with("transaction.refunded");
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
