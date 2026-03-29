package com.autobook.Exception;

public class InvalidEditRequestException extends RuntimeException {

    public InvalidEditRequestException() {
        super("Invalid edit request");
    }
}