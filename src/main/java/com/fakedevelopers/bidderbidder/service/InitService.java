package com.fakedevelopers.bidderbidder.service;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

@Service
public class InitService implements ApplicationListener<ContextRefreshedEvent> {

    ProductService productService;

    InitService(ProductService productService) {
        this.productService = productService;
    }

    public void onApplicationEvent(ContextRefreshedEvent event) {
        productService.getPopularSearchWord(10);
    }
}
