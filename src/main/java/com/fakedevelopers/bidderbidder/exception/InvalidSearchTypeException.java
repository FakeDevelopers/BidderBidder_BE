package com.fakedevelopers.bidderbidder.exception;

import org.springframework.http.HttpStatus;

public class InvalidSearchTypeException extends HttpException {

    public InvalidSearchTypeException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
