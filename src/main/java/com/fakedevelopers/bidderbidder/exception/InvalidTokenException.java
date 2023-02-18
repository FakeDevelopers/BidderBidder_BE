package com.fakedevelopers.bidderbidder.exception;

import org.springframework.http.HttpStatus;

public class InvalidTokenException extends HttpException{
    public InvalidTokenException(HttpStatus status, String message) {
        super(status, message);
    }
    public InvalidTokenException() {
        super(HttpStatus.UNAUTHORIZED, "토큰이 만료되었거나 유효하지 않는 토큰입니다");
    }
}
