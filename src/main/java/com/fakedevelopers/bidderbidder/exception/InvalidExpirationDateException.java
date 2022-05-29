package com.fakedevelopers.bidderbidder.exception;

public class InvalidExpirationDateException extends RuntimeException{
    public InvalidExpirationDateException(String message){
        super(message);
    }
}
