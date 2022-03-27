package com.fakedevelopers.ddangddangmarket.controller;

import com.fakedevelopers.ddangddangmarket.dto.UserLoginDto;
import com.fakedevelopers.ddangddangmarket.dto.UserRegisterDto;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/user")
public class UserController {

    @PostMapping("/register")
    String userRegister(@Validated UserRegisterDto userRegisterDto) {
        return "success";
    }

    @PostMapping("/login")
    String userLogin(@Validated UserLoginDto userLoginDto) {
        return "success";
    }
}
