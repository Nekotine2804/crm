package com.sofitech.hoamaimart.transaction.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ configuration: exchange + queue cho transaction-service.
 */
@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "hoamai.exchange";
    
    // Queues
    public static final String TRANSACTION_COMPLETED_QUEUE = "transaction.completed.queue";

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue transactionCompletedQueue() {
        return QueueBuilder.durable(TRANSACTION_COMPLETED_QUEUE).build();
    }

    @Bean
    public Binding transactionCompletedBinding(Queue transactionCompletedQueue, TopicExchange topicExchange) {
        return BindingBuilder
                .bind(transactionCompletedQueue)
                .to(topicExchange)
                .with("transaction.completed");
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
