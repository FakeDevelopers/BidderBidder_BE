package com.fakedevelopers.bidderbidder.exception;

import org.springframework.http.HttpStatus;

public class InvalidTickException extends HttpException {

  public InvalidTickException(String message) {
    super(HttpStatus.BAD_REQUEST, message);
  }

}
