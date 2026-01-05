package com.synchronizer.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Message model for Kafka communication.
 * This model represents the JSON structure of messages sent/received via Kafka.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Message model for Kafka communication")
public class Message {

    @Schema(description = "Unique message identifier", example = "msg-12345")
    @NotBlank(message = "Message ID cannot be blank")
    private String id;

    @Schema(description = "Message type/category", example = "USER_CREATED")
    @NotBlank(message = "Message type cannot be blank")
    private String type;

    @Schema(description = "Message payload as key-value pairs", example = "{\"userId\": \"123\", \"name\": \"John Doe\"}")
    @NotNull(message = "Payload cannot be null")
    private Map<String, Object> payload;

    @Schema(description = "Source system/service", example = "user-service")
    private String source;

    @Schema(description = "Timestamp when message was created", example = "2026-01-05T18:15:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    @Schema(description = "Message priority (1-10, 10 being highest)", example = "5")
    private Integer priority;

    @Schema(description = "Additional metadata", example = "{\"correlationId\": \"corr-123\"}")
    private Map<String, String> metadata;
}
