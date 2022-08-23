package com.fakedevelopers.bidderbidder.exception;

import org.springframework.http.HttpStatus;

public class AlreadyExpiredException extends HttpException {

  public AlreadyExpiredException(String message) {
    super(HttpStatus.BAD_REQUEST, message);
  }
}
