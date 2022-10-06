package com.fakedevelopers.bidderbidder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing
@SpringBootApplication
@EnableScheduling
@ConfigurationPropertiesScan("com.fakedevelopers.bidderbidder.properties")
public class AuctionProjectApplication {

  public static void main(String[] args) {
    SpringApplication.run(AuctionProjectApplication.class, args);
  }

}
