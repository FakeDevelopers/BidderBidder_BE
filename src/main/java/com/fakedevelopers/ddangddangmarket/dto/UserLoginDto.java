package com.fakedevelopers.ddangddangmarket.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@RequiredArgsConstructor
public class UserLoginDto {
    @Email
    @NotNull
    @NotEmpty
    private final String email;

    @NotNull
    @NotEmpty
    private final String passwd;

}
