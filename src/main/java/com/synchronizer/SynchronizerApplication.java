package com.synchronizer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for Synchronizer.
 * Kafka message synchronizer with high throughput and database persistence.
 */
@SpringBootApplication
public class SynchronizerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SynchronizerApplication.class, args);
    }
}
