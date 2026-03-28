package com.autobook.Exception;

public class EmptyPostContentException extends RuntimeException {

    public EmptyPostContentException() {
        super("Post content must not be empty");
    }
}