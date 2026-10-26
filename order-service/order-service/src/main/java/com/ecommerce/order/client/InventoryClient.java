package com.ecommerce.order.client;

import com.ecommerce.order.dto.InventoryResponse;
import com.ecommerce.order.dto.InventoryRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "inventory-service")
public interface InventoryClient
{
    @PostMapping("/inventory/reserve")
    InventoryResponse reserveInventory(@RequestBody InventoryRequest request);

    @PostMapping("/inventory/release")
    InventoryResponse releaseInventory(@RequestBody InventoryRequest request);
}