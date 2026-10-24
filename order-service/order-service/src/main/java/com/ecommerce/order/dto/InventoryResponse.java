package com.ecommerce.order.dto;

public class InventoryResponse {

    private Long productId;
    private Integer availableQuantity;
    private Integer reservedQuantity;

    public InventoryResponse() {
    }

    public Long getProductId() {
        return productId;
    }

    public Integer getAvailableQuantity() {
        return availableQuantity;
    }

    public Integer getReservedQuantity() {
        return reservedQuantity;
    }
}