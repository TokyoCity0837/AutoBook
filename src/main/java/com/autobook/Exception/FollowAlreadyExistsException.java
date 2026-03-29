package com.autobook.Exception;

public class FollowAlreadyExistsException extends RuntimeException {

    public FollowAlreadyExistsException() {
        super("Follow relationship already exists");
    }
}