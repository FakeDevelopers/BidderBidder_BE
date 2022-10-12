package com.fakedevelopers.bidderbidder.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class NaverAccessTokenResponseDto {

  private String accessToken;

  private String refreshToken;

  private String tokenType;

  private Integer expiresIn;

  private String error;

  private String errorDescription;

  @Override
  public String toString() {
    return "NaverAccessTokenResponseDto{" + "accessToken='" + accessToken + '\''
        + ", refreshToken='" + refreshToken + '\'' + ", tokenType='" + tokenType + '\''
        + ", expiresIn=" + expiresIn + ", error='" + error + '\'' + ", errorDescription='"
        + errorDescription + '\'' + '}';
  }
}
