package com.fakedevelopers.bidderbidder.exception;

import org.springframework.http.HttpStatus;

public class InvalidProductIdException extends HttpException{

    public InvalidProductIdException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
