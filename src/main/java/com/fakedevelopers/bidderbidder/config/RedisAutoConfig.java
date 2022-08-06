package com.fakedevelopers.bidderbidder.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@RequiredArgsConstructor
@Configuration
@EnableRedisRepositories
public class RedisAutoConfig {

  @Bean
  public StringRedisTemplate redisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
    StringRedisTemplate redisTemplate = new StringRedisTemplate();
    redisTemplate.setConnectionFactory(lettuceConnectionFactory);
    return redisTemplate;
  }
}
