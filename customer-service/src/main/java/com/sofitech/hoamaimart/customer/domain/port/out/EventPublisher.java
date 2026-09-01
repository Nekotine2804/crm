package com.sofitech.hoamaimart.customer.domain.port.out;

import com.sofitech.hoamaimart.customer.domain.model.Customer;

public interface EventPublisher {

    void publishCustomerCreated(Customer customer);
}
