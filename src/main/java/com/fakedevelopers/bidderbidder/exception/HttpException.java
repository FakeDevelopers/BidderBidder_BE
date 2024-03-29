package com.fakedevelopers.bidderbidder.exception;

import org.springframework.http.HttpStatus;

public class HttpException extends RuntimeException {

  public final HttpStatus status;
  public final String code; // 필요시 상세 에러코드를 넣을 예정

  public HttpException(HttpStatus status, String message) {
    super(message);
    this.status = status;
    this.code = null;
  }

  public HttpException(HttpStatus status, String code, String message) {
    super(message);
    this.status = status;
    this.code = code;
  }
}
