package com.synchronizer.controller;

import com.synchronizer.model.Message;
import com.synchronizer.model.MessageEntity;
import com.synchronizer.service.MessageProducerService;
import com.synchronizer.service.MessageQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for Kafka message operations.
 */
@RestController
@RequestMapping("/api/messages")
@Tag(name = "Messages", description = "Kafka message operations API")
@RequiredArgsConstructor
@Slf4j
public class MessageController {

    private final MessageProducerService messageProducerService;
    private final MessageQueryService messageQueryService;

    @PostMapping("/publish")
    @Operation(summary = "Publish a message to Kafka", description = "Publishes a JSON message to the Kafka topic")
    @ApiResponse(responseCode = "202", description = "Message accepted for publishing")
    @ApiResponse(responseCode = "400", description = "Invalid message format")
    public Mono<ResponseEntity<Map<String, String>>> publishMessage(
            @Valid @RequestBody Message message) {
        
        return Mono.fromFuture(messageProducerService.publishMessage(message))
                .map(result -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("messageId", message.getId());
                    response.put("status", "PUBLISHED");
                    return ResponseEntity.accepted().body(response);
                })
                .onErrorResume(ex -> {
                    log.error("Error publishing message", ex);
                    Map<String, String> error = new HashMap<>();
                    error.put("error", ex.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error));
                });
    }

    @GetMapping("/{messageId}")
    @Operation(summary = "Get message by ID", description = "Retrieves a persisted message by its ID")
    @ApiResponse(responseCode = "200", description = "Message found")
    @ApiResponse(responseCode = "404", description = "Message not found")
    public Mono<ResponseEntity<MessageEntity>> getMessageById(
            @Parameter(description = "Message ID") @PathVariable String messageId) {
        
        return Mono.fromCallable(() -> messageQueryService.findByMessageId(messageId))
                .map(optionalMessage -> optionalMessage
                        .map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build()));
    }

    @GetMapping
    @Operation(summary = "Get all messages", description = "Retrieves all persisted messages with pagination")
    @ApiResponse(responseCode = "200", description = "Messages retrieved successfully")
    public Mono<ResponseEntity<Page<MessageEntity>>> getAllMessages(
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "DESC") String direction) {
        
        return Mono.fromCallable(() -> {
            Sort.Direction sortDirection = Sort.Direction.fromString(direction);
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
            return ResponseEntity.ok(messageQueryService.findAll(pageable));
        });
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Get messages by type", description = "Retrieves messages filtered by type")
    @ApiResponse(responseCode = "200", description = "Messages retrieved successfully")
    public Flux<MessageEntity> getMessagesByType(
            @Parameter(description = "Message type") @PathVariable String type) {
        
        return Flux.fromIterable(messageQueryService.findByType(type));
    }

    @GetMapping("/source/{source}")
    @Operation(summary = "Get messages by source", description = "Retrieves messages filtered by source")
    @ApiResponse(responseCode = "200", description = "Messages retrieved successfully")
    public Flux<MessageEntity> getMessagesBySource(
            @Parameter(description = "Message source") @PathVariable String source) {
        
        return Flux.fromIterable(messageQueryService.findBySource(source));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get messages by status", description = "Retrieves messages filtered by status")
    @ApiResponse(responseCode = "200", description = "Messages retrieved successfully")
    public Flux<MessageEntity> getMessagesByStatus(
            @Parameter(description = "Message status") @PathVariable String status) {
        
        return Flux.fromIterable(messageQueryService.findByStatus(status));
    }

    @GetMapping("/recent")
    @Operation(summary = "Get recent messages", description = "Retrieves messages from the last N hours")
    @ApiResponse(responseCode = "200", description = "Messages retrieved successfully")
    public Flux<MessageEntity> getRecentMessages(
            @Parameter(description = "Number of hours") @RequestParam(defaultValue = "24") int hours) {
        
        return Flux.fromIterable(messageQueryService.findRecentMessages(hours));
    }

    @GetMapping("/stats")
    @Operation(summary = "Get message statistics", description = "Retrieves statistics about messages")
    @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully")
    public Mono<ResponseEntity<Map<String, Object>>> getStats() {
        return Mono.fromCallable(() -> {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalMessages", messageQueryService.countAll());
            stats.put("receivedMessages", messageQueryService.countByStatus("RECEIVED"));
            stats.put("processedMessages", messageQueryService.countByStatus("PROCESSED"));
            return ResponseEntity.ok(stats);
        });
    }
}
