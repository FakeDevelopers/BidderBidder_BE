package com.fakedevelopers.bidderbidder.exception;

import org.springframework.http.HttpStatus;

public class NoImageException extends HttpException {

  public NoImageException() {
    super(HttpStatus.BAD_REQUEST, "파일이 없습니다.");
  }
}
