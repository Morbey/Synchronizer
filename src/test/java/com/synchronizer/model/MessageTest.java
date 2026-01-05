package com.synchronizer.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Message model.
 */
class MessageTest {

    @Test
    void testMessageBuilder() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("key", "value");
        
        Map<String, String> metadata = new HashMap<>();
        metadata.put("correlationId", "test-123");
        
        LocalDateTime now = LocalDateTime.now();
        
        Message message = Message.builder()
                .id("msg-123")
                .type("TEST_MESSAGE")
                .payload(payload)
                .source("test-service")
                .timestamp(now)
                .priority(5)
                .metadata(metadata)
                .build();
        
        assertNotNull(message);
        assertEquals("msg-123", message.getId());
        assertEquals("TEST_MESSAGE", message.getType());
        assertEquals(payload, message.getPayload());
        assertEquals("test-service", message.getSource());
        assertEquals(now, message.getTimestamp());
        assertEquals(5, message.getPriority());
        assertEquals(metadata, message.getMetadata());
    }
    
    @Test
    void testMessageEquality() {
        Message message1 = Message.builder()
                .id("msg-123")
                .type("TEST")
                .payload(new HashMap<>())
                .build();
        
        Message message2 = Message.builder()
                .id("msg-123")
                .type("TEST")
                .payload(new HashMap<>())
                .build();
        
        assertEquals(message1, message2);
    }
}
