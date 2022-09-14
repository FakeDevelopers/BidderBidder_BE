package com.fakedevelopers.bidderbidder.exception;

import org.springframework.http.HttpStatus;

public class InvalidImageIdException extends HttpException {

    public InvalidImageIdException() {
        super(HttpStatus.BAD_REQUEST, "존재하지 않는 이미지 번호 입니다.");
    }
}
