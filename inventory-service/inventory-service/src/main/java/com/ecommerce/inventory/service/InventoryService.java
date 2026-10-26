package com.ecommerce.inventory.service;

import com.ecommerce.inventory.dto.InventoryRequest;
import com.ecommerce.inventory.dto.InventoryResponse;
import com.ecommerce.inventory.entity.Inventory;
import com.ecommerce.inventory.repository.InventoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService
{
    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional
    public InventoryResponse addInventory(InventoryRequest request)
    {
        Inventory inventory = inventoryRepository
                        .findByProductId(request.getProductId())
                        .orElse(new Inventory(
                                request.getProductId(), 0, 0)
                        );

        inventory.setAvailableQuantity(
                inventory.getAvailableQuantity() + request.getQuantity()
        );

        Inventory saved = inventoryRepository.save(inventory);
        return toResponse(saved);
    }

    @Transactional
    public InventoryResponse reserveInventory(InventoryRequest request)
    {
        Inventory inventory = inventoryRepository
                        .findByProductId(request.getProductId())
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Inventory not found for product: "
                                                + request.getProductId()
                                )
                        );

        if (inventory.getAvailableQuantity() < request.getQuantity())
        {
            throw new RuntimeException("Insufficient inventory");
        }

        inventory.setAvailableQuantity(
                inventory.getAvailableQuantity() - request.getQuantity()
        );

        inventory.setReservedQuantity(
                inventory.getReservedQuantity() + request.getQuantity()
        );

        return toResponse(inventoryRepository.save(inventory));
    }

    @Transactional
    public InventoryResponse releaseInventory(InventoryRequest request)
    {
        Inventory inventory =
                inventoryRepository
                        .findByProductId(request.getProductId())
                        .orElseThrow(
                                () -> new RuntimeException("Inventory not found")
                        );

        if (inventory.getReservedQuantity() < request.getQuantity())
        {
            throw new RuntimeException("Cannot release more than reserved quantity");
        }

        inventory.setReservedQuantity(
                inventory.getReservedQuantity() - request.getQuantity()
        );

        inventory.setAvailableQuantity(
                inventory.getAvailableQuantity() + request.getQuantity()
        );

        return toResponse(inventoryRepository.save(inventory));
    }

    public InventoryResponse getInventory(Long productId)
    {
        Inventory inventory =
                inventoryRepository
                        .findByProductId(productId)
                        .orElseThrow(
                                () -> new RuntimeException("Inventory not found")
                        );

        return toResponse(inventory);
    }

    private InventoryResponse toResponse(Inventory inventory)
    {
        return new InventoryResponse(
                inventory.getProductId(),
                inventory.getAvailableQuantity(),
                inventory.getReservedQuantity()
        );
    }
}