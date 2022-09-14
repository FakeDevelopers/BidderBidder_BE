package com.fakedevelopers.bidderbidder.exception;

import org.springframework.http.HttpStatus;

public class InvalidProductIdException extends HttpException {

    public InvalidProductIdException() {
        super(HttpStatus.BAD_REQUEST, "존재하지 않는 게시글 번호입니다.");
    }
}
