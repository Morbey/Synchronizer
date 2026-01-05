package com.synchronizer.service;

import com.synchronizer.model.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Service for publishing messages to Kafka.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MessageProducerService {

    private final KafkaTemplate<String, Message> kafkaTemplate;
    private static final String TOPIC = "synchronizer-messages";

    /**
     * Publish a message to Kafka topic.
     * 
     * @param message the message to publish
     * @return CompletableFuture for async result
     */
    public CompletableFuture<Void> publishMessage(Message message) {
        // Ensure message has ID and timestamp
        if (message.getId() == null || message.getId().isEmpty()) {
            message.setId(UUID.randomUUID().toString());
        }
        if (message.getTimestamp() == null) {
            message.setTimestamp(LocalDateTime.now());
        }

        log.info("Publishing message to Kafka: id={}, type={}", message.getId(), message.getType());

        return kafkaTemplate.send(TOPIC, message.getId(), message)
                .thenApply(result -> {
                    log.info("Message published successfully: id={}, partition={}, offset={}", 
                            message.getId(), 
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                    return null;
                })
                .exceptionally(ex -> {
                    log.error("Failed to publish message: id={}", message.getId(), ex);
                    throw new RuntimeException("Failed to publish message", ex);
                });
    }

    /**
     * Publish a message synchronously.
     * 
     * @param message the message to publish
     */
    public void publishMessageSync(Message message) {
        publishMessage(message).join();
    }
}
