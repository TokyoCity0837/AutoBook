package com.autobook.Exception;

public class EditRequestNotFoundException extends RuntimeException {

    public EditRequestNotFoundException(Long editRequestId) {
        super("Edit request not found with id: " + editRequestId);
    }
}