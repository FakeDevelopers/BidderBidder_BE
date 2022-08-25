package com.fakedevelopers.bidderbidder.exception;

import org.springframework.http.HttpStatus;

public class InvalidExtensionException extends HttpException {

    public InvalidExtensionException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
