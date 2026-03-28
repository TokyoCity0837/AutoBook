package com.autobook.Exception;

public class InvalidFollowException extends RuntimeException {

    public InvalidFollowException() {
        super("Invalid follow request");
    }
}