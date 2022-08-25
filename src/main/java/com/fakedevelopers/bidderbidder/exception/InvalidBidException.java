package com.fakedevelopers.bidderbidder.exception;

import org.springframework.http.HttpStatus;

public class InvalidBidException extends HttpException {

  public InvalidBidException(String message) {
    super(HttpStatus.BAD_REQUEST, message);
  }
}
