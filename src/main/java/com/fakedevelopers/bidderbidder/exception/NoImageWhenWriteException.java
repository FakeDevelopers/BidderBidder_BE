package com.fakedevelopers.bidderbidder.exception;

import org.springframework.http.HttpStatus;

public class NoImageWhenWriteException extends HttpException {

    public NoImageWhenWriteException() {
        super(HttpStatus.BAD_REQUEST, "파일이 없습니다.");
    }
}
