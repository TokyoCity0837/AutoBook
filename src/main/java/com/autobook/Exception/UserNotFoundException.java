package com.autobook.Exception;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String exception) {
        super(exception);
    }
}
