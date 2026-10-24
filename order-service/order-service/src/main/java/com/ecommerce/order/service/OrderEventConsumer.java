
package com.ecommerce.order.service;

import com.ecommerce.order.entity.ProcessedEvent;
import com.ecommerce.order.repository.ProcessedEventRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OrderEventConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(OrderEventConsumer.class);

    private final ProcessedEventRepository processedEventRepository;
    private final ObjectMapper objectMapper;

    public OrderEventConsumer(ProcessedEventRepository processedEventRepository, ObjectMapper objectMapper) {
        this.processedEventRepository = processedEventRepository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
            topics = "order-events",
            groupId = "order-event-consumer-group"
    )
    @Transactional
    public void consumeOrderEvent(String message) throws Exception
    {
        JsonNode event = objectMapper.readTree(message);
        String eventId = event.path("eventId").asText(null);

        if (eventId == null || eventId.isBlank()) {
            throw new IllegalArgumentException(
                    "Kafka event is missing eventId");
        }

        if (processedEventRepository.existsById(eventId)) {
            log.info("Skipping duplicate event. eventId={}", eventId);
            return;
        }

        processedEventRepository.saveAndFlush(new ProcessedEvent(eventId));

        log.info("Processing order event. eventId={}, message={}", eventId, message);

        // Current demo: record the event and log it.
        // Add actual business processing here later.
    }
}
