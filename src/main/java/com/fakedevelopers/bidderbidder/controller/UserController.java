package com.fakedevelopers.bidderbidder.controller;

import com.fakedevelopers.bidderbidder.dto.UserLoginDto;
import com.fakedevelopers.bidderbidder.dto.UserRegisterDto;
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
