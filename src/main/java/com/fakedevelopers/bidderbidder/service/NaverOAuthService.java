package com.fakedevelopers.bidderbidder.service;

import static com.fakedevelopers.bidderbidder.domain.Constants.MAX_USERNAME_SIZE;
import static java.lang.Math.min;

import com.fakedevelopers.bidderbidder.domain.OAuthProfile.NAVER;
import com.fakedevelopers.bidderbidder.domain.OAuthProfile.NAVER.GrantType;
import com.fakedevelopers.bidderbidder.dto.NaverAccessTokenResponseDto;
import com.fakedevelopers.bidderbidder.dto.NaverUserInfoDto;
import com.fakedevelopers.bidderbidder.exception.NaverApiException;
import com.fakedevelopers.bidderbidder.model.UserEntity;
import com.fakedevelopers.bidderbidder.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class NaverOAuthService {

  private final UserRepository userRepository;
  private final OAuth2UserService oAuth2UserService;

  public void validateRequestFormat(String code, String error,
      String errorDescription) throws NaverApiException {
    if ((code == null ) == (error == null)) {
      throw new NaverApiException(
          "code와 error는 베타적이여야한다.(request format error);" + "code: " + code + ", error: " + error);
    }
    if (error != null) {
      throw new NaverApiException(errorDescription);
    }
  }


  public NaverAccessTokenResponseDto getAccessToken(String clientId, String clientSecret,
      String code, String state) {
    WebClient webClient = WebClient.builder()
        .baseUrl(NAVER.REQUEST_TOKEN_URL).build();

    return webClient.get()
        .uri(uriBuilder -> uriBuilder
            .queryParam("grant_type", GrantType.AUTHORIZATION_CODE)
            .queryParam("client_id", clientId)
            .queryParam("client_secret", clientSecret)
            .queryParam("code", code)
            .queryParam("state", state)
            .build())
        .retrieve()
        .bodyToMono(NaverAccessTokenResponseDto.class)
        .block();
  }

  public NaverUserInfoDto getUserInfo(String accessToken) {
    String encodedAccessToken = URLEncoder.encode(accessToken, StandardCharsets.UTF_8);
    WebClient webClient = WebClient.builder()
        .baseUrl(NAVER.REQUEST_USERINFO_URL)
        .build();
    return webClient.get()
        .header("Authorization", "Bearer: " + encodedAccessToken)
        .retrieve()
        .bodyToMono(NaverUserInfoDto.class)
        .block();
  }

  public String makeFirebaseCustomToken(@NotNull String accessToken, String prefix)
      throws FirebaseAuthException {

    NaverUserInfoDto userInfo = getUserInfo(accessToken);
    validateDto(userInfo);

    String userId = userInfo.getResponse().getId();
    String targetUsername = OAuth2UserService.makeUsernameWithPrefix(prefix, userId);
    Optional<UserEntity> target = userRepository.findByUsername(targetUsername);
    if (target.isEmpty()) {
      oAuth2UserService.register(userInfo.toOAuth2UserRegisterDto());
    }
    Map<String, Object> additionalClaims = new HashMap<>();
    additionalClaims.put("id", userInfo.getResponse().getId());
    additionalClaims.put("provider", prefix);

    return FirebaseAuth.getInstance()
        .createCustomToken(targetUsername, additionalClaims);
  }

  public void validateDto(@NotNull NaverAccessTokenResponseDto dto) throws NaverApiException {
    if (Optional.ofNullable(dto.getError()).isPresent()) {
      throw new NaverApiException("access token 발급 실패" + dto.getErrorDescription());
    }
  }

  public void validateDto(@NotNull NaverUserInfoDto dto) throws NaverApiException {
    if (!dto.getMessage().equals("success")) {
      throw new NaverApiException("유저 정보 조회 불가; " + dto.getResultCode() + "; " + dto.getMessage());
    }
  }

}
