package com.fakedevelopers.bidderbidder.dto;

import com.fakedevelopers.bidderbidder.domain.Constants;
import com.fakedevelopers.bidderbidder.domain.OAuthProfile.NAVER;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NaverUserInfoDto {

  @JsonProperty(value = "resultcode")
  String resultCode;

  String message;

  NaverAccount response;

  public OAuth2UserRegisterDto toOAuth2UserRegisterDto() {
    return OAuth2UserRegisterDto.builder().username(NAVER.PREFIX + response.getId())
        .email(response.getEmail())
        .nickname(Constants.INIT_NICKNAME)
        .serviceProvider(NAVER.TITLE).build();
  }

  @Override
  public String toString() {
    return "NaverUserInfoDto{" +
        "resultCode='" + resultCode + '\'' +
        ", message='" + message + '\'' +
        ", response=" + response +
        '}';
  }
}
