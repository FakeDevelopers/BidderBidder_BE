package com.fakedevelopers.bidderbidder.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KakaoAccessTokenResponseDto {

  @JsonProperty(value = "token_type")
  private String tokenType;

  @JsonProperty(value = "access_token")
  private String accessToken;

  @JsonProperty(value = "expires_in")
  private Integer expiresIn;

  @JsonProperty(value = "refresh_token")
  private String refreshToken;

  @JsonProperty(value = "refresh_token_expires_in")
  private Integer refreshTokenExpiresIn;

  @Override
  public String toString() {
    return "KakaoAccessTokenResponseDto{" + "tokenType='" + tokenType + '\'' + ", accessToken='"
        + accessToken + '\'' + ", expiresIn=" + expiresIn + ", refreshToken='" + refreshToken + '\''
        + ", refreshTokenExpiresIn=" + refreshTokenExpiresIn + '}';
  }
}
