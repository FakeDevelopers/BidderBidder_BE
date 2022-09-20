package com.fakedevelopers.bidderbidder.exception;

import org.springframework.http.HttpStatus;

public class InvalidCategoryException extends HttpException {

  public InvalidCategoryException(String message) {
    super(HttpStatus.BAD_REQUEST, message);
  }
}
