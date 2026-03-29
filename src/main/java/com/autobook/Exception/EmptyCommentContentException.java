package com.autobook.Exception;

public class EmptyCommentContentException extends RuntimeException {

    public EmptyCommentContentException() {
        super("Comment content must not be empty");
    }
}