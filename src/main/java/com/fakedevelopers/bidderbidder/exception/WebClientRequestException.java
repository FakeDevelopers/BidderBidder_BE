package com.fakedevelopers.bidderbidder.exception;

import org.springframework.http.HttpStatus;

public class WebClientRequestException extends HttpException {

  public WebClientRequestException(HttpStatus status, String message) {
    super(status, message);
  }

  public WebClientRequestException(HttpStatus status, String code, String message) {
    super(status, code, message);
  }
}
