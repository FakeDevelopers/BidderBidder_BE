package com.fakedevelopers.bidderbidder.dto;


import com.fakedevelopers.bidderbidder.domain.Constants;
import com.fakedevelopers.bidderbidder.domain.OAuthProfile.KAKAO;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The type Kakao user info dto. 카카오 동의 항목
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KakaoUserInfoDto {

  // username(not-null), email(nullable), nickname(not-null), password(nullable)
  @NotNull
  private Long id;

  private KakaoAccount kakaoAccount; // 이메일 정보가 포함된다.


  public OAuth2UserRegisterDto toOAuth2UserRegisterDto() {
    return OAuth2UserRegisterDto.builder().username(KAKAO.PREFIX + id)
        .email(kakaoAccount.getEmail())
        .nickname(Constants.INIT_NICKNAME) // 닉네임은 반드시 중복 방지를 위하여 추가작업이 필요하다.
        .serviceProvider(KAKAO.TITLE).build();
  }
}
