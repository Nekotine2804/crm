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
    public static final String QUEUE = "loyalty.transaction.queue";
    public static final String ROUTING_KEY = "transaction.completed";

    // TopicExchange
    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(EXCHANGE);
    }

    // Queue
    @Bean
    public Queue loyaltyQueue() {
        return QueueBuilder.durable(QUEUE).build();
    }

    // Binding: queue -> exchange với routing key
    @Bean
    public Binding loyaltyBinding(Queue loyaltyQueue, TopicExchange topicExchange) {
        return BindingBuilder
                .bind(loyaltyQueue)
                .to(topicExchange)
                .with(ROUTING_KEY);
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
