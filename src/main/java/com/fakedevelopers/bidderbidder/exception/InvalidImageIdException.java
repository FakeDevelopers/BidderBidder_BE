package com.fakedevelopers.bidderbidder.exception;

import org.springframework.http.HttpStatus;

public class InvalidImageIdException extends HttpException{

    public InvalidImageIdException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
