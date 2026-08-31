package com.sofitech.hoamaimart.transaction.domain.port.out;

import com.sofitech.hoamaimart.transaction.domain.model.Transaction;

/**
 * Port OUT: event publisher interface.
 * Implement bởi adapter RabbitMQ.
 */
public interface EventPublisher {

    void publishTransactionCompleted(Transaction transaction);
}