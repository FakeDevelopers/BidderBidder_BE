package com.fakedevelopers.bidderbidder.controller;

import com.fakedevelopers.bidderbidder.domain.Constants;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * User와 관련된 컨트롤러 정의 1. 회원가입 2. 로그인 3. Oauth 로그인
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

  private final FirebaseAuth firebaseAuth;
  private final OAuth2UserService oAuth2UserService;


  @PostMapping("/register")
  String userRegister(@Validated UserRegisterDto userRegisterDto) {
    return Constants.SUCCESS;
  }


  @PostMapping("/login")
  String userLogin(@Validated UserLoginDto userLoginDto) {
    return Constants.SUCCESS;
  }


  @PostMapping("/signin-google")
  UserInfo oAuth2GoogleLoginOrRegister(@RequestHeader("Authorization") String authorization) {
    // Authorization: Bearer <token> 형식
    final FirebaseToken decodedToken;
    // 추후에 토큰 유효성 검사는 Filter 처리
    try {
      decodedToken = RequestUtil.parseJwtToken(authorization);
    } catch (FirebaseAuthException | IllegalArgumentException e) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰" + e.getMessage());
    }

    // token을 이용하여 가져온 사용자 정보를 통하여, 회원가입 및 로그인 절차를 진행한다.
    UserEntity userEntity = oAuth2UserService.loadUserOrRegister(decodedToken);
    return new UserInfo(userEntity);
  }
}
