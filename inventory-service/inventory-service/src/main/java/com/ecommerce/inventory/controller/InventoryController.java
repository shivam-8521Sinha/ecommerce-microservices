package com.ecommerce.inventory.controller;

import com.ecommerce.inventory.dto.InventoryRequest;
import com.ecommerce.inventory.dto.InventoryResponse;
import com.ecommerce.inventory.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
public class InventoryController
{
    private final InventoryService inventoryService;
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InventoryResponse addInventory(@Valid @RequestBody InventoryRequest request)
    {
        return inventoryService.addInventory(request);
    }

    @PostMapping("/reserve")
    public InventoryResponse reserveInventory(@Valid @RequestBody InventoryRequest request)
    {
        return inventoryService.reserveInventory(request);
    }

    @PostMapping("/release")
    public InventoryResponse releaseInventory(@Valid @RequestBody InventoryRequest request)
    {
        return inventoryService.releaseInventory(request);
    }

    @GetMapping("/{productId}")
    public InventoryResponse getInventory(@PathVariable Long productId)
    {
        return inventoryService.getInventory(productId);
    }
}