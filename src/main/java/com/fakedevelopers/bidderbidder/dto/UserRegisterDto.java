package com.fakedevelopers.bidderbidder.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@RequiredArgsConstructor
public class UserRegisterDto {
    @Email(message = "이메일의 형식을 따라야 합니다.")
    @NotNull(message = "이메일 필드가 정의되어있지않습니다.")
    @NotEmpty(message = "이메일을 입력해주세요")
    @NotBlank(message = "이메일에 빈칸은 입력불가입니다.")
    private final String email;

    @NotNull(message = "닉네임 필드가 정의되어있지않습니다.")
    @NotEmpty(message = "닉네임을 입력해주세요")
    @NotBlank(message = "닉네임에 빈칸은 입력불가 입니다.")
    @Length(min = 3, max = 12)
    private final String nickname;

    @NotNull(message = "비밀번호 필드가 정의되어있지않습니다.")
    @NotEmpty(message = "비밀번호를 입력해주세요")
    @NotBlank(message = "비밀번호에 빈칸은 입력불가 입니다.")
    private final String passwd;
}
