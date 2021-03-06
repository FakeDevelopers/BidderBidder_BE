package com.fakedevelopers.bidderbidder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class AuctionProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuctionProjectApplication.class, args);
    }

}
