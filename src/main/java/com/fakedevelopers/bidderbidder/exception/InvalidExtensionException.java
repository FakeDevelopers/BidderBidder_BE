package com.fakedevelopers.bidderbidder.exception;

import org.springframework.http.HttpStatus;

public class InvalidExtensionException extends HttpException {

    public InvalidExtensionException() {
        super(HttpStatus.BAD_REQUEST, "파일 확장자가 다릅니다.");
    }
}
