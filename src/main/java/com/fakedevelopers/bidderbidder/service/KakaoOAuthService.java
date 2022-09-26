package com.fakedevelopers.bidderbidder.service;

import com.fakedevelopers.bidderbidder.dto.KakaoAccessTokenResponseDto;
import com.fakedevelopers.bidderbidder.dto.KakaoTokenValidationResponseDto;
import com.fakedevelopers.bidderbidder.dto.KakaoUserInfoDto;
import com.fakedevelopers.bidderbidder.exception.HttpException;
import com.fakedevelopers.bidderbidder.exception.KakaoApiException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class KakaoOAuthService {

  public boolean validateRequestFormat(String code, String error) throws KakaoApiException {
    // 1. Request 자체가 조작되었다.
    if (code == null && error == null) {
      throw new HttpException(HttpStatus.BAD_REQUEST, "잘못된 요청");
    }
    // 2. 사용자가 권한 인증을 수행하지 않았거나 로그인 과정이 실패한 경우
    if (error != null) {
      throw new HttpException(HttpStatus.BAD_REQUEST, error);
    }
    return true;
  }

  public KakaoAccessTokenResponseDto getAccessToken(String code) {
    // 카카오 oauth server로 부터 code를 이용하여 access_token을 얻기 위한 과정
    WebClient webClient = WebClient.builder().baseUrl("https://kauth.kakao.com")
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        .build();

    // https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html#webflux-client
    MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
    formData.add("grant_type", "authorization_code");
    formData.add("client_id", "cd5499f387c4ffb03b5928946765e699");
    formData.add("redirect_uri", "http://localhost:8080/user/signin-kakao");
    formData.add("code", code);

    // access token 정보가 response에 저장된다.
    return webClient.post().uri("/oauth/token").body(BodyInserters.fromFormData(formData))
        .retrieve().bodyToMono(KakaoAccessTokenResponseDto.class).block();
  }

  public boolean validateAccessToken(KakaoAccessTokenResponseDto dto) throws KakaoApiException {
    KakaoTokenValidationResponseDto validationResponse = WebClient.create("https://kapi.kakao.com")
        .get().uri("/v1/user/access_token_info")
        .header("Authorization", "Bearer " + dto.getAccess_token()).retrieve()
        .bodyToMono(KakaoTokenValidationResponseDto.class).block();

    // 에러 코드가 존재할 경우 처리
    if (Optional.ofNullable(validationResponse.getCode()).isPresent()) {
      throw new KakaoApiException(validationResponse.getCode());
    }
    return true;
  }

  private KakaoUserInfoDto getUserInfo(String accessToken) {
    WebClient webClient = WebClient.builder().baseUrl("https://kapi.kakao.com")
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        .build();

    return webClient.get().uri("/v2/user/me").header("Authorization", "Bearer " + accessToken)
        .retrieve().bodyToMono(KakaoUserInfoDto.class).block();
  }

  // todo: 미완결된 메소드
  public String makeFirebaseCustomToken(String accessToken)
      throws FirebaseAuthException {
    KakaoUserInfoDto userInfo = getUserInfo(accessToken);
    Map<String, Object> additionalClaims = new HashMap<>();
    additionalClaims.put("provider", "kakao");
    return FirebaseAuth.getInstance()
        .createCustomToken(String.valueOf(userInfo.getId()), additionalClaims);
  }
}
