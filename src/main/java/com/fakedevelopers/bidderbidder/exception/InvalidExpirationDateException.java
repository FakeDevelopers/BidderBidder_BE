package com.fakedevelopers.bidderbidder.exception;

import org.springframework.http.HttpStatus;

public class InvalidExpirationDateException extends HttpException {

    public InvalidExpirationDateException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
