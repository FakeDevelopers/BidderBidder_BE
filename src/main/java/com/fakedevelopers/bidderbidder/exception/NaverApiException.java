package com.fakedevelopers.bidderbidder.exception;

public class NaverApiException extends RuntimeException {

  public NaverApiException(String message) {
    super("Naver API 오류: " + message);
  }
}
