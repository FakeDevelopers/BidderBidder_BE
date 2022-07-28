package com.fakedevelopers.bidderbidder.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;

/** OAuth2UserRegisterDto: OAuth2 로그인시에 필요한 정보들을 정의.
 *  <br>
 *  #33 기준 필요한 정보: Email, nickname(nullable)
 */
@Getter
@RequiredArgsConstructor
public class OAuth2UserRegisterDto {

  @Email(message = "이메일의 형식을 따라야 합니다.")
  @NotBlank(message = "이메일에는 공백문자가 포함될 수 없습니다")
  private final String email;

  @NotBlank(message = "형식이 잘못되었습니다.")
  @Length(min = 3, max = 12)
  private final String nickname;
}
