package com.fakedevelopers.bidderbidder.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends HttpException {

  public UserNotFoundException(long id) {
    super(HttpStatus.NOT_FOUND, "user not found userId : " + id);
  }
}
