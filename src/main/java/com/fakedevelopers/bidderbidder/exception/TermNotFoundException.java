package com.fakedevelopers.bidderbidder.exception;

import org.springframework.http.HttpStatus;

public class TermNotFoundException extends HttpException {

  public TermNotFoundException() {
    super(HttpStatus.NOT_FOUND, "존재하지 않는 약관입니다.");
  }
}
