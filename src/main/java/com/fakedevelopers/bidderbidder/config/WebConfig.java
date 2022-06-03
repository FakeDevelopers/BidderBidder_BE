package com.fakedevelopers.bidderbidder.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://116.40.180.205:8080", "http://localhost:80")
                .allowedMethods("GET", "POST")
                .maxAge(3000);
    }
}
