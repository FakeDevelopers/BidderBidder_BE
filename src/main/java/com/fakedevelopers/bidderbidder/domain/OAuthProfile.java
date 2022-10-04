package com.fakedevelopers.bidderbidder.domain;

import static com.fakedevelopers.bidderbidder.domain.Constants.UTILITY_CLASS;

public class OAuthProfile {

  private OAuthProfile() {
    throw new AssertionError(UTILITY_CLASS);
  }

  public static class KAKAO extends OAuthProfile {

    private KAKAO() {
      super();
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

  public static class GOOGLE extends OAuthProfile {

    private GOOGLE() {
      super();
    }

    public static final String PREFIX = "google_";
  }

  public static class NAVER extends OAuthProfile {

    private NAVER() {
      super();
    }

    public static final String PREFIX = "naver_";

  }

  public static class APPLE extends OAuthProfile {

    private APPLE() {
      super();
    }

    public static final String PREFIX = "apple_";

  }
}
