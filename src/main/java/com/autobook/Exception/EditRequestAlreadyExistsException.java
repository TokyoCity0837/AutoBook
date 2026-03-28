package com.autobook.Exception;

public class EditRequestAlreadyExistsException extends RuntimeException {

    public EditRequestAlreadyExistsException() {
        super("Edit request already exists");
    }
}