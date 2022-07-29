package com.fakedevelopers.bidderbidder.service;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class GetPopularSearchWordAtStart implements ApplicationListener<ContextRefreshedEvent> {

  private final ProductService productService;

  GetPopularSearchWordAtStart(ProductService productService) {
    this.productService = productService;
  }

  public void onApplicationEvent(ContextRefreshedEvent event) {
    productService.getPopularSearchWord(10);
  }
}
