package com.fakedevelopers.bidderbidder.exception;

import org.springframework.http.HttpStatus;

public class ExistedUserException extends HttpException {

  public ExistedUserException(HttpStatus status, String message) {
    super(status, message);
  }

  public ExistedUserException() {
    super(HttpStatus.CONFLICT, "이미 존재하는 회원입니다");
  }
}
