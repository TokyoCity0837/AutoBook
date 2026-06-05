package com.autobook.Exception;

/**
 * Intermediate exception grouping all User-related errors (Hierarchy Level 2).
 * <p>
 * Extends {@link BaseAutoBookException} and overrides {@link #getErrorCode()}
 * to return the category-level code {@code "USER"}. Concrete user exceptions
 * extend this class and further specialise the error code (Level 3).
 * </p>
 */
public class UserException extends BaseAutoBookException {

    /**
     * Constructs a new UserException with the given message.
     *
     * @param message human-readable error description
     */
    public UserException(String message) {
        super(message);
    }

    /**
     * Returns the category-level error code for all user-related errors.
     *
     * @return {@code "USER"}
     */
    @Override
    public String getErrorCode() {
        return "USER";
    }
}
