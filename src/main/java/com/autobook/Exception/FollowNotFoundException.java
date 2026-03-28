package com.autobook.Exception;

public class FollowNotFoundException extends RuntimeException {

    public FollowNotFoundException(Long followId) {
        super("Follow not found with id: " + followId);
    }

    public FollowNotFoundException() {
        super("Follow relationship not found");
    }
}