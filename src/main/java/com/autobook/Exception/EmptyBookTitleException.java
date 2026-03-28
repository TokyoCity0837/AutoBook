package com.autobook.Exception;

public class EmptyBookTitleException extends RuntimeException {

    public EmptyBookTitleException() {
        super("Book title must not be empty");
    }
}