package com.ecommerce.order.client;

import com.ecommerce.order.dto.PaymentRequest;
import com.ecommerce.order.dto.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "payment-service")
public interface PaymentClient {

    @PostMapping("/payments")
    PaymentResponse processPayment(
            @RequestBody PaymentRequest request
    );

    @PostMapping("/payments/{orderId}/refund")
    PaymentResponse refundPayment(
            @PathVariable("orderId") Long orderId
    );
}