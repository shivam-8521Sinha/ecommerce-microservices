
package com.ecommerce.order.service;

import com.ecommerce.order.entity.Order;
import com.ecommerce.order.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderFailureService {

    private final OrderRepository orderRepository;

    public OrderFailureService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markOrderFailed(Long orderId)
    {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Order not found: " + orderId));

        order.setStatus("FAILED");
        order.setSagaStatus("FAILED");
        orderRepository.save(order);
    }
}
