package com.fakedevelopers.bidderbidder.repository;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

@Component
public class RedisRepository {

    RedisTemplate<String, Object> redisTemplate;

    RedisRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void save(String str) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        valueOperations.set("testValue", str);
    }
}
