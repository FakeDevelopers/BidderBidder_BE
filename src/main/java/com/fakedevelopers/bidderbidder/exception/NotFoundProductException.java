package com.fakedevelopers.bidderbidder.exception;

import org.springframework.http.HttpStatus;

public class NotFoundProductException extends HttpException {

    public NotFoundProductException() {
        super(HttpStatus.NOT_FOUND, "존재하지 않는 게시글 번호입니다.");
    }
}
