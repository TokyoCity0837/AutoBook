package com.autobook.Exception;

/**
 * Thrown when a {@link com.autobook.Social.User.User} cannot be found (Hierarchy Level 3).
 * Extends {@link UserException} → {@link BaseAutoBookException} → {@code RuntimeException}.
 */
public class UserNotFoundException extends UserException {

    public UserNotFoundException(String message) {
        super(message);
    }

    @Override
    public String getErrorCode() {
        return "USER_NOT_FOUND";
    }
}

