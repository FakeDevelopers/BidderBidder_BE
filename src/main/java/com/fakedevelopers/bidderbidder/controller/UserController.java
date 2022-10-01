package com.fakedevelopers.bidderbidder.controller;

import com.fakedevelopers.bidderbidder.domain.Constants;
import com.fakedevelopers.bidderbidder.domain.OAuthProfile.GOOGLE;
import com.fakedevelopers.bidderbidder.domain.OAuthProfile.KAKAO;
import com.fakedevelopers.bidderbidder.dto.KakaoAccessTokenResponseDto;
import com.fakedevelopers.bidderbidder.dto.UserLoginDto;
import com.fakedevelopers.bidderbidder.dto.UserRegisterDto;
import com.fakedevelopers.bidderbidder.exception.KakaoApiException;
import com.fakedevelopers.bidderbidder.message.response.UserInfo;
import com.fakedevelopers.bidderbidder.model.UserEntity;
import com.fakedevelopers.bidderbidder.service.KakaoOAuthService;
import com.fakedevelopers.bidderbidder.service.OAuth2UserService;
import com.fakedevelopers.bidderbidder.util.RequestUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
  private final KakaoOAuthService kakaoOAuthService;


  @PostMapping("/register")
  String userRegister(@Validated UserRegisterDto userRegisterDto) {
    return Constants.SUCCESS;
  }


  @PostMapping("/login")
  String userLogin(@Validated UserLoginDto userLoginDto) {
    return Constants.SUCCESS;
  }


  @PostMapping("/signin-google")
  public UserInfo oAuth2GoogleLoginOrRegister(
      @RequestHeader("Authorization") String authorization) {
    // Authorization: Bearer <token> 형식
    final FirebaseToken decodedToken;
    // 추후에 토큰 유효성 검사는 Filter 처리
    try {
      decodedToken = RequestUtil.parseJwtToken(authorization);
    } catch (FirebaseAuthException | IllegalArgumentException e) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰\n" + e.getMessage());
    }

    // token을 이용하여 가져온 사용자 정보를 통하여, 회원가입 및 로그인 절차를 진행한다.
    UserEntity userEntity = oAuth2UserService.loadUserOrRegister(GOOGLE.PREFIX, decodedToken);
    return new UserInfo(userEntity);
  }

  /**
   * <h1> signin-kakao </h1>
   *
   * @param code  카카오 인증 서버에서 redirect uri 로 보내주는 authorization code
   * @param error 클라이언트가 카카오 인증 서버로 보내는 요청에 문제가 발생했을 경우, 존재하는 값. 에러 메세지에 대한 자세한 내용은 kakao developer
   *              API 문서를 참고
   * @return User Credential에 해당하는 custom token을 반환
   * <br> 클라이언트는 signInWithCustomToken의 결과로 받아온 인스턴스에서 getIdToken() 메소드를 통해 firebase id token을 얻을 수
   * 있다.
   * @see <a
   * href=https://firebase.google.com/docs/auth/admin/create-custom-tokens?hl=ko#web-version-9>커스텀
   * 토큰 활용 방법</a>
   */
  @GetMapping("/signin-kakao")
  public String oAuth2KakaoLoginOrRegister(
      @RequestParam(value = "code", required = false) String code,
      @RequestParam(value = "error", required = false) String error) {

    try {
      kakaoOAuthService.validateRequestFormat(code, error);
    } catch (KakaoApiException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 접근\n" + e.getMessage());
    }

    // access token과 additional claims를 받아온다.
    KakaoAccessTokenResponseDto responseDto = kakaoOAuthService.getAccessToken(code);

    // 해당 access token을 검증한다.
    try {
      kakaoOAuthService.validateAccessToken(responseDto);
    } catch (KakaoApiException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Invalid Access Token!!!\n" + e.getMessage());
    }

    // access token을 이용하여, firebase custom token을 만든다.
    String accessToken = responseDto.getAccess_token();
    String firebaseCustomToken;
    try {
      firebaseCustomToken = kakaoOAuthService.makeFirebaseCustomToken(accessToken, KAKAO.TITLE);
    } catch (FirebaseAuthException e) {
      throw new KakaoApiException(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
    return firebaseCustomToken;
  }


}
