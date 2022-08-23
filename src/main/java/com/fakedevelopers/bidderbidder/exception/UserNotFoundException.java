package com.fakedevelopers.bidderbidder.exception;

public class UserNotFoundException extends RuntimeException {

  public UserNotFoundException(long id) {
    super("user not found userId : " + id);
  }
}
