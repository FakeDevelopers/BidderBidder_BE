package com.fakedevelopers.bidderbidder.exception;

public class KakaoApiException extends RuntimeException{
  private Integer errorCode;
  public KakaoApiException(Integer code) {
    // https://developers.kakao.com/docs/latest/ko/reference/rest-api-reference
    super("카카오 서버 오류");
    this.errorCode = code;
  }
}
