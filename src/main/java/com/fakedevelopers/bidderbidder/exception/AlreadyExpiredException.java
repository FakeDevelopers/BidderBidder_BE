package com.fakedevelopers.bidderbidder.exception;

public class AlreadyExpiredException extends RuntimeException {

  public AlreadyExpiredException(String message) {
    super(message);
  }
}
