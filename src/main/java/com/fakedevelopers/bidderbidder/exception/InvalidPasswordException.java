package com.fakedevelopers.bidderbidder.exception;

import org.springframework.http.HttpStatus;

public class InvalidPasswordException extends HttpException{
    public InvalidPasswordException(HttpStatus status, String message) {
        super(status, message);
    }
    public InvalidPasswordException() {
        super(HttpStatus.UNAUTHORIZED, "패스워드가 잘못되었습니다");
    }
}
