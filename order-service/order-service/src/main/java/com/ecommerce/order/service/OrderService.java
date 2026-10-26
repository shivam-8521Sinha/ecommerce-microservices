
package com.ecommerce.order.service;

import com.ecommerce.order.client.InventoryClient;
import com.ecommerce.order.client.PaymentClient;
import com.ecommerce.order.client.ProductClient;
import com.ecommerce.order.dto.*;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OutboxEvent;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.order.repository.OutboxEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final InventoryClient inventoryClient;
    private final PaymentServiceClient paymentServiceClient;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;
    private final OrderFailureService orderFailureService;


    public OrderService(
            OrderRepository orderRepository,
            ProductClient productClient,
            InventoryClient inventoryClient,
            PaymentServiceClient paymentServiceClient,
            OutboxEventRepository outboxEventRepository,
            ObjectMapper objectMapper,
            OrderFailureService orderFailureService
    ) {
        this.orderRepository = orderRepository;
        this.productClient = productClient;
        this.inventoryClient = inventoryClient;
        this.paymentServiceClient = paymentServiceClient;
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
        this.orderFailureService = orderFailureService;
    }

    @Transactional
    public Order createOrder(CreateOrderRequest request)
    {
        // 1. Get product information
        ProductResponse product = productClient.getProductById(request.getProductId());

        // 2. Calculate total amount
        BigDecimal totalAmount = product.getPrice().multiply(
                BigDecimal.valueOf(request.getQuantity())
        );

        // 3. Reserve inventory
        InventoryRequest inventoryRequest = new InventoryRequest(
                request.getProductId(),
                request.getQuantity()
        );

        // 4. Create and save order
        Order order = new Order();

        order.setProductId(request.getProductId());
        order.setQuantity(request.getQuantity());
        order.setTotalAmount(totalAmount);
        order.setStatus("PENDING_PAYMENT");
        order.setSagaStatus("STARTED");

        order = orderRepository.save(order);

        try {
            inventoryClient.reserveInventory(inventoryRequest);
            order.setSagaStatus("INVENTORY_RESERVED");
            orderRepository.save(order);
        } catch (Exception e) {
            orderFailureService.markOrderFailed(order.getId());
            throw new RuntimeException(
                    "Inventory reservation failed for order " + order.getId(), e);
        }

        // 6. Save event in the SAME database transaction
        OutboxEvent outboxEvent = new OutboxEvent(
                "ORDER_CREATED",
                "Order",
                order.getId().toString(),
                "{}"
        );

        outboxEvent =outboxEventRepository.save(outboxEvent);

        // Now create the final payload with eventId
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("eventId", outboxEvent.getId());
        eventData.put("orderId", order.getId());
        eventData.put("productId", order.getProductId());
        eventData.put("quantity", order.getQuantity());
        eventData.put("totalAmount", order.getTotalAmount());
        eventData.put("status", order.getStatus());


        try {
            String payload = objectMapper.writeValueAsString(eventData);
            outboxEvent.setPayload(payload);
            outboxEventRepository.save(outboxEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize order event", e);
        }

        // 7. Process payment using the existing flow
        PaymentRequest paymentRequest =
                new PaymentRequest(order.getId(), totalAmount);

        PaymentResponse payment =
                paymentServiceClient.processPayment(paymentRequest);

        // 8. Update order status
        if ("SUCCESS".equals(payment.getStatus())) {
            order.setStatus("CONFIRMED");
            order.setSagaStatus("COMPLETED");
        } else {
            order.setStatus("PAYMENT_PENDING");
            order.setSagaStatus("PAYMENT_PENDING");
        }

        return orderRepository.save(order);
    }
}
