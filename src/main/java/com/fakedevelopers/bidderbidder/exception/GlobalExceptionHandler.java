package com.fakedevelopers.bidderbidder.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.sentry.Sentry;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(Exception.class)
  protected ResponseEntity<ErrorResponse> exceptionHandler(Exception e) {
    Sentry.captureException(e);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, e.getMessage()));
  }

  @ExceptionHandler(HttpException.class)
  protected ResponseEntity<ErrorResponse> httpExceptionHandler(HttpException e) {
    Sentry.captureException(e);
    return ResponseEntity.status(e.status)
        .body(new ErrorResponse(e.status.value(), e.code, e.getMessage()));
  }

  @Getter
  @JsonInclude(Include.NON_NULL)
  private static class ErrorResponse {

    private final int status;
    private final String message;
    private final String code;

    ErrorResponse(int status, String code, String message) {
      this.status = status;
      this.message = message;
      this.code = code;
    }
  }
}
