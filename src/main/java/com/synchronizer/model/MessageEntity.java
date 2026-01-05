package com.synchronizer.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity class for persisting messages in the database.
 */
@Entity
@Table(name = "messages", indexes = {
    @Index(name = "idx_message_type", columnList = "type"),
    @Index(name = "idx_message_timestamp", columnList = "timestamp"),
    @Index(name = "idx_message_source", columnList = "source")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dbId;

    @Column(nullable = false, unique = true)
    private String messageId;

    @Column(nullable = false)
    private String type;

    @Column(columnDefinition = "TEXT")
    private String payload;

    @Column
    private String source;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column
    private Integer priority;

    @Column(columnDefinition = "TEXT")
    private String metadata;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime processedAt;

    @Column
    private String status;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = "RECEIVED";
        }
    }
}
