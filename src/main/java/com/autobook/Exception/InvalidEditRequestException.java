package com.autobook.Exception;

public class InvalidEditRequestException extends RuntimeException {

    public InvalidEditRequestException() {
        super("Invalid edit request");
    }

    public InvalidEditRequestException(String string) {
        super("Invalid edit request" + string);
    }
}