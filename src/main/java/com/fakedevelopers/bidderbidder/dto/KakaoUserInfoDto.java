package com.fakedevelopers.bidderbidder.dto;

// TODO: 고민중!! 이걸 따로 테이블을 만들어야하나?

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
public class KakaoUserInfoDto {
  // username(not-null), email(nullable), nickname(not-null), password(nullable)
  private Long id;
  private KakaoAccount kakao_account; // 이메일 정보가 포함된다.

}
