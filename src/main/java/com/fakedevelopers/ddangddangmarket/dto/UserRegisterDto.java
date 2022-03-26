package com.fakedevelopers.ddangddangmarket.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@RequiredArgsConstructor
public class UserRegisterDto {
    @Email
    @NotNull
    @NotEmpty
    private final String email;

    @NotNull
    @NotEmpty
    @Length(min = 3, max = 12)
    private final String nickname;

    @NotNull
    @NotEmpty
    private final String passwd;
}
