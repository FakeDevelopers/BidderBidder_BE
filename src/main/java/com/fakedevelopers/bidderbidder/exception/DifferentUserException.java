package com.fakedevelopers.bidderbidder.exception;

import org.springframework.http.HttpStatus;

public class DifferentUserException extends HttpException {

  public DifferentUserException(String message) {
    super(HttpStatus.FORBIDDEN, message);
  }
}
