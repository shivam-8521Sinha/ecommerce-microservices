package com.ecommerce.order.service;

import com.ecommerce.order.client.PaymentClient;
import com.ecommerce.order.dto.PaymentRequest;
import com.ecommerce.order.dto.PaymentResponse;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceClient {
    private final PaymentClient paymentClient;
    private final CircuitBreakerFactory<?, ?> circuitBreakerFactory;

    public PaymentServiceClient(PaymentClient paymentClient, CircuitBreakerFactory<?, ?> circuitBreakerFactory) {
        this.paymentClient = paymentClient;
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    public PaymentResponse processPayment(PaymentRequest request) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("paymentService");
        return circuitBreaker.run(() -> paymentClient.processPayment(request),
                throwable -> paymentFallback(request, throwable));
    }

    private PaymentResponse paymentFallback(PaymentRequest request, Throwable throwable) {

        System.out.println("Payment Service unavailable: " + throwable.getMessage());
        PaymentResponse response = new PaymentResponse();

        response.setOrderId(request.getOrderId());
        response.setAmount(request.getAmount());
        response.setStatus("PAYMENT_PENDING");

        return response;
    }
}
