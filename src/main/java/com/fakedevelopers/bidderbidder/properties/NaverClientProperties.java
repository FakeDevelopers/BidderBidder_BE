package com.fakedevelopers.bidderbidder.properties;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@RequiredArgsConstructor
@ConfigurationProperties(prefix = "oauth2.naver.client")
@ConstructorBinding
public class NaverClientProperties {
  private final String id;
  private final String secret;

  public String getId() {
    return id;
  }

  public String getSecret() {
    return secret;
  }
}
