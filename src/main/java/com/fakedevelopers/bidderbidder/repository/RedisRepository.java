package com.fakedevelopers.bidderbidder.repository;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisRepository {

    StringRedisTemplate redisTemplate;

    RedisRepository(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
}
