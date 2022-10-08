package com.fakedevelopers.bidderbidder.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NaverAccessTokenResponseDto {

  @JsonProperty(value = "access_token")
  private String accessToken;

  @JsonProperty(value = "refresh_token")
  private String refreshToken;

  @JsonProperty(value = "token_type")
  private String tokenType;

  @JsonProperty(value = "expires_in")
  private Integer expiresIn;

  private String error;

  @JsonProperty(value = "error_description")
  private String errorDescription;

  @Override
  public String toString() {
    return "NaverAccessTokenResponseDto{" +
        "accessToken='" + accessToken + '\'' +
        ", refreshToken='" + refreshToken + '\'' +
        ", tokenType='" + tokenType + '\'' +
        ", expiresIn=" + expiresIn +
        ", error='" + error + '\'' +
        ", errorDescription='" + errorDescription + '\'' +
        '}';
  }
}
