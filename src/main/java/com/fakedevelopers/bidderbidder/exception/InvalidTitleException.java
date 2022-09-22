package com.fakedevelopers.bidderbidder.exception;

import org.springframework.http.HttpStatus;

public class InvalidTitleException extends HttpException {

  public InvalidTitleException(String message) {
    super(HttpStatus.BAD_REQUEST, message);
  }

}
