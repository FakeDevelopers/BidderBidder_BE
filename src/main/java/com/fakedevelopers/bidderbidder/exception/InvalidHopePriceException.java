package com.fakedevelopers.bidderbidder.exception;

import org.springframework.http.HttpStatus;

public class InvalidHopePriceException extends HttpException {

    public InvalidHopePriceException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
