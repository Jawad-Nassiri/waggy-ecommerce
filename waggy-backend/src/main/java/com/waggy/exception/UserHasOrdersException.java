package com.waggy.exception;

public class UserHasOrdersException extends RuntimeException{
    public UserHasOrdersException(String message) {
        super(message);
    }
}
