package com.synchronizer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Basic application context test.
 */
@SpringBootTest
@TestPropertySource(properties = {
    "spring.cloud.stream.kafka.binder.brokers=localhost:9092",
    "spring.kafka.bootstrap-servers=localhost:9092"
})
class SynchronizerApplicationTests {

    @Test
    void contextLoads() {
        // This test verifies that the Spring application context loads successfully
    }
}
