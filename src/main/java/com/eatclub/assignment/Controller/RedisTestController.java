package com.eatclub.assignment.Controller;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/redis-test")
public class RedisTestController {

    private final StringRedisTemplate redisTemplate;

    public RedisTestController(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/set")
    public String setKey() {
        redisTemplate.opsForValue().set("foo", "bar");
        return "Key 'foo' set to 'bar'";
    }

    @GetMapping("/get")
    public String getKey() {
        String value = redisTemplate.opsForValue().get("foo");
        return "Value for 'foo': " + value;
    }
}