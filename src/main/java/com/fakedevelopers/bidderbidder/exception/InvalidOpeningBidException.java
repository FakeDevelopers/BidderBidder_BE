package com.fakedevelopers.bidderbidder.exception;

import org.springframework.http.HttpStatus;

public class InvalidOpeningBidException extends HttpException {

    public InvalidOpeningBidException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }

}
