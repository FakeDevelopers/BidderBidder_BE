package com.fakedevelopers.bidderbidder.exception;

import org.springframework.http.HttpStatus;

public class NotFoundImageException extends HttpException {

    public NotFoundImageException() {
        super(HttpStatus.NOT_FOUND, "존재하지 않는 이미지 번호 입니다.");
    }
}
