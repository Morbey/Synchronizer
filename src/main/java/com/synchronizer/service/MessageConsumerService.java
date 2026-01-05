package com.synchronizer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synchronizer.model.Message;
import com.synchronizer.model.MessageEntity;
import com.synchronizer.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for consuming Kafka messages and persisting them to the database.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MessageConsumerService {

    private final MessageRepository messageRepository;
    private final ObjectMapper objectMapper;

    /**
     * Process incoming Kafka messages.
     * This method is called by Spring Cloud Stream for each message consumed.
     */
    @Transactional
    public void processMessage(Message message) {
        try {
            log.info("Consuming message: id={}, type={}", message.getId(), message.getType());
            
            // Convert message to entity and persist
            MessageEntity entity = convertToEntity(message);
            messageRepository.save(entity);
            
            log.info("Message persisted successfully: id={}", message.getId());
            
            // Mark as processed
            entity.setProcessedAt(LocalDateTime.now());
            entity.setStatus("PROCESSED");
            messageRepository.save(entity);
            
        } catch (Exception e) {
            log.error("Error processing message: id={}", message.getId(), e);
            throw new RuntimeException("Failed to process message", e);
        }
    }

    private MessageEntity convertToEntity(Message message) throws JsonProcessingException {
        return MessageEntity.builder()
                .messageId(message.getId())
                .type(message.getType())
                .payload(objectMapper.writeValueAsString(message.getPayload()))
                .source(message.getSource())
                .timestamp(message.getTimestamp() != null ? message.getTimestamp() : LocalDateTime.now())
                .priority(message.getPriority())
                .metadata(message.getMetadata() != null ? objectMapper.writeValueAsString(message.getMetadata()) : null)
                .status("RECEIVED")
                .build();
    }
}
