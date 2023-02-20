package com.fakedevelopers.bidderbidder.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * The type User login dto.
 */
@Getter
@Setter
@RequiredArgsConstructor
public class UserLoginDto {

  @NotNull(message = "아이디 필드가 정의되어 있지 않습니다")
  @NotEmpty(message = "아이디를 입력해주세요")
  @NotBlank(message = "아이디에 빈칸이 포함될 수 없습니다")
  private final String username;

  @NotNull(message = "비밀번호 필드가 정의되어있지않습니다.")
  @NotEmpty(message = "비밀번호를 입력해주세요")
  @NotBlank(message = "비밀번호에 빈칸은 입력불가 입니다.")
  private final String password;

}
