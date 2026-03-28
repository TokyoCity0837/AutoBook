package com.autobook.Exception;

public class EmailAlreadyInUseException extends RuntimeException {
    public EmailAlreadyInUseException(String email){
        super("This email is already in use " + email);
    }
}
