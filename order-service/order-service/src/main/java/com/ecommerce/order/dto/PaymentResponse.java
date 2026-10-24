package com.ecommerce.order.dto;

import java.math.BigDecimal;

public class PaymentResponse {

    private Long id;
    private Long orderId;
    private BigDecimal amount;
    private String status;

    public PaymentResponse() {
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }
}