package com.fakedevelopers.bidderbidder.exception;

import org.springframework.http.HttpStatus;

public class ModifyProductException extends HttpException {

  public ModifyProductException(String message) {
    super(HttpStatus.BAD_REQUEST, message);
  }
}
