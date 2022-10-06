package com.fakedevelopers.bidderbidder.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

/**
 * <h1> 운영환경에 따라 유동적인 OAuth2 관련 String Resource를 표현 </h1>
 * * 각각의 properties들은 <a href="https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config.typesafe-configuration-properties">spring docs</a>
 * 의 규칙에 따라 값을 가져온다.
 *
 */
@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "oauth2")
@ConstructorBinding
public class OAuth2Properties {

  private final Redirect redirect;

  @Getter
  @RequiredArgsConstructor
  public static final class Redirect {

    private final Base base;

    @Getter
    @RequiredArgsConstructor
    public static final class Base {

      private final String uri;
    }
  }
}
