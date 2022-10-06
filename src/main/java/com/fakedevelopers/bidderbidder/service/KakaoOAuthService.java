package com.fakedevelopers.bidderbidder.service;

import com.fakedevelopers.bidderbidder.domain.OAuthProfile.KAKAO;
import com.fakedevelopers.bidderbidder.dto.KakaoAccessTokenResponseDto;
import com.fakedevelopers.bidderbidder.dto.KakaoTokenValidationResponseDto;
import com.fakedevelopers.bidderbidder.dto.KakaoUserInfoDto;
import com.fakedevelopers.bidderbidder.exception.HttpException;
import com.fakedevelopers.bidderbidder.exception.KakaoApiException;
import com.fakedevelopers.bidderbidder.exception.WebClientRequestException;
import com.fakedevelopers.bidderbidder.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * <h1> 카카오 OAuth 로그인/회원가입 서비스 </h1>
 * <p>
 * * 1. kakao auth server로 부터 redirect된 후 authorization code를 이용하여, access 토큰 발급 <br> * 2. access
 * token을 검증 (additional claim을 확인, 만료 일자 등등..) <br> * 3. 유효한 access token을 이용하여 유저 정보를
 * `KakaoUserInfoDto`로 받아온다 <br> * 4. KakaoUserInfoDto를 적절하게 OAuthRegisterDto로 변환한다음,
 * OAuth2UserService에게 위임한다 <br>
 * </p>
 */
@Service
@RequiredArgsConstructor
public class KakaoOAuthService {

  private final OAuth2UserService oAuth2UserService;
  private final UserRepository userRepository;

  public void validateRequestFormat(String code, String error) throws KakaoApiException {
    // 1. Request 자체가 조작되었다.
    if (code == null && error == null) {
      throw new HttpException(HttpStatus.BAD_REQUEST, "잘못된 요청");
    }
    // 2. 사용자가 권한 인증을 수행하지 않았거나 로그인 과정이 실패한 경우
    if (error != null) {
      throw new HttpException(HttpStatus.UNAUTHORIZED, error);
    }
  }

  public KakaoAccessTokenResponseDto getAccessToken(String code) {
    // 카카오 oauth server로 부터 code를 이용하여 access_token을 얻기 위한 과정
    WebClient webClient = WebClient.builder().baseUrl(KAKAO.KAUTH_BASE_URL)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        .build();

    // https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html#webflux-client
    MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
    formData.add("grant_type", KAKAO.GRANT_TYPE);
    formData.add("client_id", KAKAO.CLIENT_ID);
    formData.add("redirect_uri",
        oAuth2UserService.getBaseRedirectURI() + KAKAO.REDIRECT_URI); // 실제 서버의 URI
    formData.add("code", code);

    // access token 정보가 response에 저장된다.
    return webClient.post().uri(KAKAO.REQUEST_TOKEN_URL).body(BodyInserters.fromFormData(formData))
        .retrieve().bodyToMono(KakaoAccessTokenResponseDto.class).block();
  }

  public void validateAccessToken(KakaoAccessTokenResponseDto dto)
      throws KakaoApiException, WebClientRequestException {
    KakaoTokenValidationResponseDto validationResponse = WebClient.create(KAKAO.KAPI_BASE_URL)
        .get().uri(KAKAO.REQUEST_TOKENINFO_URL)
        .header("Authorization", "Bearer " + dto.getAccess_token()).retrieve()
        .bodyToMono(KakaoTokenValidationResponseDto.class).block();
    if (validationResponse == null) {
      throw new WebClientRequestException(HttpStatus.BAD_REQUEST, "WebClient 오류");
    }
    // 에러 코드가 존재할 경우 처리
    if (Optional.ofNullable(validationResponse.getCode()).isPresent()) {
      throw new KakaoApiException(validationResponse.getCode());
    }
  }

  private KakaoUserInfoDto getUserInfo(String accessToken) {
    WebClient webClient = WebClient.builder().baseUrl(KAKAO.KAPI_BASE_URL)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        .build();

    return webClient.get().uri(KAKAO.REQUEST_USERINFO_URL)
        .header("Authorization", "Bearer " + accessToken)
        .retrieve().bodyToMono(KakaoUserInfoDto.class).block();
  }

  /**
   * *
   *
   * @param accessToken     service Provider가 제공하는 액세스 토큰
   * @param serviceProvider custom token의 claim으로 들어갈 서비스 제공자의 이름
   * @return accessToken의 uid와 Service provider에 해당하는 커스텀 토큰을 생성한다.
   * @throws FirebaseAuthException 파이어베이스 연동 중 발생하는 오류
   */
  public String makeFirebaseCustomToken(@NotNull String accessToken, String serviceProvider)
      throws FirebaseAuthException {

    KakaoUserInfoDto userInfo = getUserInfo(accessToken);
    if (userInfo == null) {
      throw new WebClientRequestException(HttpStatus.BAD_REQUEST, "WebClient 오류");
    }
    if (userRepository.findByUsername(serviceProvider + userInfo.getId())
        .isEmpty()) {

      oAuth2UserService.register(userInfo.toOAuth2UserRegisterDto());
    }

    Map<String, Object> additionalClaims = new HashMap<>();
    additionalClaims.put("id", userInfo.getId());
    additionalClaims.put("provider", serviceProvider);

    return FirebaseAuth.getInstance()
        .createCustomToken(serviceProvider + userInfo.getId(), additionalClaims);
  }
}
