package com.ecommerce.payment.service;

import com.ecommerce.payment.dto.PaymentRequest;
import com.ecommerce.payment.entity.Payment;
import com.ecommerce.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public Payment processPayment(PaymentRequest request) {

        Payment payment = paymentRepository
                .findByOrderId(request.getOrderId())
                .orElse(null);

        // Idempotency:
        // If payment already exists for this order,
        // return the existing payment instead of creating another one.
        if (payment != null) {
            return payment;
        }
        payment = new Payment(request.getOrderId(), request.getAmount(), "SUCCESS");

        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment refundPayment(Long orderId) {

        Payment payment = paymentRepository
                .findByOrderId(orderId)
                .orElseThrow(
                        () -> new RuntimeException("Payment not found")
                );

        payment.setStatus("REFUNDED");

        return paymentRepository.save(payment);
    }
}