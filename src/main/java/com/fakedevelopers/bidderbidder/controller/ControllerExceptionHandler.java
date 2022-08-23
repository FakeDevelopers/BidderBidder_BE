package com.fakedevelopers.bidderbidder.controller;

import com.fakedevelopers.bidderbidder.exception.HttpException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerExceptionHandler {

  @ExceptionHandler(Exception.class)
  protected ResponseEntity<ErrorResponse> exceptionHandler(Exception e) {
    return new ResponseEntity<>(
        new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, e.getMessage()),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(HttpException.class)
  protected ResponseEntity<ErrorResponse> httpExceptionHandler(HttpException e) {
    return new ResponseEntity<>(
        new ErrorResponse(e.status.value(), e.code, e.getMessage()),
        e.status);
  }

  private static class ErrorResponse {

    final int status;
    final String message;

    final String code;

    ErrorResponse(int status, String message, String code) {
      this.status = status;
      this.message = message;
      this.code = code;
    }
  }
}
