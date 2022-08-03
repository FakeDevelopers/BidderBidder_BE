package com.fakedevelopers.bidderbidder.service;

import com.fakedevelopers.bidderbidder.repository.RedisRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class InitService implements ApplicationListener<ContextRefreshedEvent> {

  private final ProductService productService;
  private final RedisRepository redisRepository;

  InitService(ProductService productService, RedisRepository redisRepository) {
    this.productService = productService;
    this.redisRepository = redisRepository;
  }

  public void onApplicationEvent(ContextRefreshedEvent event) {
    redisRepository.getSearchWords();
    productService.getPopularSearchWord(10);
  }
}
