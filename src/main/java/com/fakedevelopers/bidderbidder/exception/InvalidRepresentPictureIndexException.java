package com.fakedevelopers.bidderbidder.exception;

import org.springframework.http.HttpStatus;

public class InvalidRepresentPictureIndexException extends HttpException {

    public InvalidRepresentPictureIndexException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
