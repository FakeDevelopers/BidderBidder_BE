package com.fakedevelopers.bidderbidder.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KakaoAccessTokenResponseDto {

  private String tokenType;

  private String accessToken;

  private Integer expiresIn;

  private String refreshToken;

  private Integer refreshTokenExpiresIn;

  @Override
  public String toString() {
    return "KakaoAccessTokenResponseDto{" + "tokenType='" + tokenType + '\'' + ", accessToken='"
        + accessToken + '\'' + ", expiresIn=" + expiresIn + ", refreshToken='" + refreshToken + '\''
        + ", refreshTokenExpiresIn=" + refreshTokenExpiresIn + '}';
  }
}
