package com.synchronizer.service;

import com.synchronizer.model.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Spring Cloud Stream functional bindings for Kafka.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaStreamBindings {

    private final MessageConsumerService messageConsumerService;

    /**
     * Consumer function for incoming messages.
     * Automatically bound to the input channel defined in application.yaml.
     */
    @Bean
    public Consumer<Message> messageConsumer() {
        return message -> {
            log.debug("Received message from Kafka stream: {}", message.getId());
            messageConsumerService.processMessage(message);
        };
    }

    /**
     * Producer function placeholder for outbound messages.
     * This can be used with StreamBridge for dynamic message publishing.
     */
    @Bean
    public Supplier<Message> messageProducer() {
        // This is a placeholder - actual publishing is done via KafkaTemplate
        // to have more control over the publishing process
        return () -> null;
    }
}
