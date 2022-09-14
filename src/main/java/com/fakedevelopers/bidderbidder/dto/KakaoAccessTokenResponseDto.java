package com.fakedevelopers.bidderbidder.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KakaoAccessTokenResponseDto {

  private String token_type;
  private String access_token;
  private Integer expires_in;
  private String refresh_token;
  private Integer refresh_token_expires_in;

  @Override
  public String toString() {
    return "KakaoAccessTokenResponseDto{" +
        "token_type='" + token_type + '\'' +
        ", access_token='" + access_token + '\'' +
        ", expires_in=" + expires_in +
        ", refresh_token='" + refresh_token + '\'' +
        ", refresh_token_expires_in=" + refresh_token_expires_in +
        '}';
  }
}
