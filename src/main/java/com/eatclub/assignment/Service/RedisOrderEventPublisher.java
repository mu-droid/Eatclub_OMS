package com.eatclub.assignment.Service;

import com.eatclub.assignment.Entity.OrderStatus;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class RedisOrderEventPublisher {
    private static final String STREAM_KEY = "orders:stream";
    private final StringRedisTemplate redisTemplate;

    public RedisOrderEventPublisher(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void publishOrderEvent(UUID orderId, OrderStatus status, String customerId) {
        Map<String, String> body = new HashMap<>();
        body.put("orderId", orderId.toString());
        body.put("status", status.name());
        body.put("customerId", customerId);
        body.put("timestamp", Instant.now().toString());

        // add to stream
        RecordId id = redisTemplate.opsForStream().add(STREAM_KEY, body);
        // optional: log id
    }
}