package com.eatclub.assignment.Stream;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OrderEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderEventConsumer.class);

    private final StringRedisTemplate redisTemplate;
    private final StreamMessageListenerContainer<String, ?> container;

    private static final String STREAM_KEY = "orders:stream";
    private static final String GROUP = "analytics-consumers";
    private static final String CONSUMER = "consumer-1";

    public OrderEventConsumer(StringRedisTemplate redisTemplate,
                              StreamMessageListenerContainer<String, ?> container) {
        this.redisTemplate = redisTemplate;
        this.container = container;
    }

    @PostConstruct
    public void init() {
        // Ensure consumer group exists. Use latest as start offset to avoid reprocessing historical data.
        try {
            // createGroup throws if group exists -> catch and ignore
            redisTemplate.opsForStream().createGroup(STREAM_KEY, ReadOffset.latest(), GROUP);
            log.info("Created Redis stream group '{}' on '{}'", GROUP, STREAM_KEY);
        } catch (Exception ex) {
            log.debug("Could not create group (probably already exists): {}", ex.getMessage());
        }

        // Register a listener on the container using the consumer-group API.
        container.receive(Consumer.from(GROUP, CONSUMER),
                StreamOffset.create(STREAM_KEY, ReadOffset.lastConsumed()),
                message -> {

                    Object value = message.getValue();
                    if (value instanceof MapRecord) {
                        @SuppressWarnings("unchecked")
                        MapRecord<String, String, String> record = (MapRecord<String, String, String>) value;
                        Map<String, String> data = record.getValue();
                        log.info("Consumed order event. id={} data={}", record.getId(), data);


                        try {
                            redisTemplate.opsForStream().acknowledge(STREAM_KEY, GROUP, record.getId());
                        } catch (Exception ackEx) {
                            log.error("Failed to acknowledge message {}: {}", record.getId(), ackEx.getMessage());
                        }

                    } else {
                        log.warn("Received unexpected record type: {}", value == null ? "null" : value.getClass());
                    }
                });


        container.start();
        log.info("Started StreamMessageListenerContainer for stream '{}'", STREAM_KEY);
    }
}