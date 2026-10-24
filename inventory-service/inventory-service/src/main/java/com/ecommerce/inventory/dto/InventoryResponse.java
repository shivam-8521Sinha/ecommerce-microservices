package com.ecommerce.inventory.dto;

public class InventoryResponse
{
    private Long productId;
    private Integer availableQuantity;
    private Integer reservedQuantity;

    public InventoryResponse() {
    }

    public InventoryResponse(Long productId, Integer availableQuantity, Integer reservedQuantity)
    {
        this.productId = productId;
        this.availableQuantity = availableQuantity;
        this.reservedQuantity = reservedQuantity;
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