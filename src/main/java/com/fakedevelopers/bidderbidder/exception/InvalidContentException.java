package com.fakedevelopers.bidderbidder.exception;

import org.springframework.http.HttpStatus;

public class InvalidContentException extends HttpException {

    public InvalidContentException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }

}
