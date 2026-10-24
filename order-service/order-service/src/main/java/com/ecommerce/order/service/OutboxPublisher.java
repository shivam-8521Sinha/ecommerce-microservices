
package com.ecommerce.order.service;

import com.ecommerce.order.entity.OutboxEvent;
import com.ecommerce.order.repository.OutboxEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class OutboxPublisher {

    private static final Logger log =
            LoggerFactory.getLogger(OutboxPublisher.class);

    private static final String TOPIC = "order-events";

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public OutboxPublisher(OutboxEventRepository outboxEventRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.outboxEventRepository = outboxEventRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelay = 5000)
    public void publishPendingEvents()
    {
        List<OutboxEvent> events = outboxEventRepository.findByStatus("PENDING");

        for (OutboxEvent event : events) {
            try {
                // Send the event and wait for Kafka acknowledgement.
                kafkaTemplate.send(
                        TOPIC,
                        event.getAggregateId(),
                        event.getPayload()
                ).get(10, TimeUnit.SECONDS);

                // Mark as published only after acknowledgement.
                event.setStatus("PUBLISHED");
                outboxEventRepository.save(event);

                log.info(
                        "Published outbox event id={} type={} aggregateId={}",
                        event.getId(),
                        event.getEventType(),
                        event.getAggregateId()
                );

            } catch (Exception exception) {
                // Leave it PENDING so a later scheduled run can retry.
                log.error(
                        "Failed to publish outbox event id={}. It remains PENDING.",
                        event.getId(),
                        exception
                );
            }
        }
    }
}
