package com.fakedevelopers.bidderbidder.exception;

import org.springframework.http.HttpStatus;

public class InvalidSearchTypeException extends HttpException {

    public InvalidSearchTypeException() {
        super(HttpStatus.BAD_REQUEST, "잘못된 검색 타입입니다.");
    }
}
