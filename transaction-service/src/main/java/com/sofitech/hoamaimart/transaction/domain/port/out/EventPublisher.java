package com.sofitech.hoamaimart.transaction.domain.port.out;

import com.sofitech.hoamaimart.transaction.domain.model.Transaction;


public interface EventPublisher {

    void publishTransactionCompleted(Transaction transaction);

    void publishTransactionRefunded(Transaction transaction, String reason);
}
