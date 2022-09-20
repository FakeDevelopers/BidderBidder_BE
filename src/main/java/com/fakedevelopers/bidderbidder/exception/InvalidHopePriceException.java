package com.fakedevelopers.bidderbidder.exception;

import org.springframework.http.HttpStatus;

public class InvalidHopePriceException extends HttpException {

  public InvalidHopePriceException() {
    super(HttpStatus.BAD_REQUEST, "희망가가 시작가보다 적습니다");
  }
}
