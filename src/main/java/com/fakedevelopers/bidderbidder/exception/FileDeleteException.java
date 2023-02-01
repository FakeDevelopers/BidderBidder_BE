package com.fakedevelopers.bidderbidder.exception;

import org.springframework.http.HttpStatus;

public class FileDeleteException extends HttpException {

  public FileDeleteException(String message) {
    super(HttpStatus.BAD_REQUEST, message);
  }

}
