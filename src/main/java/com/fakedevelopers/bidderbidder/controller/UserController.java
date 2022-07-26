package com.fakedevelopers.bidderbidder.controller;

import com.fakedevelopers.bidderbidder.dto.OAuth2UserRegisterDto;
import com.fakedevelopers.bidderbidder.dto.UserLoginDto;
import com.fakedevelopers.bidderbidder.dto.UserRegisterDto;
import com.fakedevelopers.bidderbidder.message.response.UserInfo;
import com.fakedevelopers.bidderbidder.model.UserEntity;
import com.fakedevelopers.bidderbidder.service.OAuth2UserService;
import com.fakedevelopers.bidderbidder.util.RequestUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

  private final FirebaseAuth firebaseAuth;
  private final OAuth2UserService oAuth2UserService; // OAuth2에서 사용자 정보를 얻기 위함

  @PostMapping("/register")
  String userRegister(@Validated UserRegisterDto userRegisterDto) {
    return "success";
  }


  @PostMapping("/login")
  String userLogin(@Validated UserLoginDto userLoginDto) {
    return "success";
  }

  @PostMapping("/signin-google")
  UserInfo oAuth2GoogleLoginOrRegister(@RequestHeader("Authorization") String authorization) {
    // Authorization: Bearer <token> 형식
    FirebaseToken decodedToken;
    final String INIT_NICK_NAME = "신규 유저";

    try {
      String token = RequestUtil.getAuthorizationToken(authorization);
      decodedToken = firebaseAuth.verifyIdToken(token);
    } catch (FirebaseAuthException | IllegalArgumentException e) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰" + e.getMessage());
    }

    UserEntity userEntity;
    try {
      userEntity = (UserEntity) oAuth2UserService.loadUserByUsername(decodedToken.getEmail());
      if (oAuth2UserService.findByEmail(userEntity.getEmail()) == null) {
        // 이전에 등록된 적이 없는 경우, 등록 과정 수행
        userEntity = oAuth2UserService.register(
            new OAuth2UserRegisterDto(decodedToken.getEmail(), INIT_NICK_NAME)
        );
      } else {
        userEntity = oAuth2UserService.findByEmail(userEntity.getEmail());
      }
    } catch (UsernameNotFoundException e) {
      return null;
    }
    return new UserInfo(userEntity.getEmail(), userEntity.getNickname());
  }
}
