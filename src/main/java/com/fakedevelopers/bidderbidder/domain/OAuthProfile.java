package com.fakedevelopers.bidderbidder.domain;

import static com.fakedevelopers.bidderbidder.domain.Constants.*;

public class OAuthProfile {

  private OAuthProfile() {
    throw new IllegalStateException(UTILITY_CLASS);
  }

  public static class KAKAO {

    private KAKAO() {
      throw new IllegalStateException(UTILITY_CLASS);
    }

    public static final String TITLE = "KAKAO";
    public static final String PREFIX = "kakao_";
    public static final String GRANT_TYPE = "authorization_code";
    public static final String CLIENT_ID = "cd5499f387c4ffb03b5928946765e699";
    public static final String LOCAL_REDIRECT_URI = "http://localhost:8080/user/signin-kakao";
    public static final String SANDBOX_REDIRECT_URI = "http://bidderbidderapi.kro.kr:8080/user/signin-kakao";
    public static final String REAL_REDIRECT_URI = "http://bidderbidderapi.kro.kr:80/user/signin-kakao";

    public static final String KAUTH_BASE_URL = "https://kauth.kakao.com";
    public static final String REQUEST_TOKEN_URL = "/oauth/token";

    public static final String KAPI_BASE_URL = "https://kapi.kakao.com";
    public static final String REQUEST_USERINFO_URL = "/v2/user/me";
    public static final String REQUEST_TOKENINFO_URL = "/v1/user/access_token_info";
  }

  public static class GOOGLE {

    private GOOGLE() {
      throw new IllegalStateException(UTILITY_CLASS);
    }

    public static final String PREFIX = "google_";
  }

  public static class NAVER {

    private NAVER() {
      throw new IllegalStateException(UTILITY_CLASS);
    }

    public static final String PREFIX = "naver_";

  }

  public static class APPLE {

    private APPLE() {
      throw new IllegalStateException(UTILITY_CLASS);
    }

    public static final String PREFIX = "apple_";

  }
}
