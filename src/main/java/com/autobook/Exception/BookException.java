package com.autobook.Exception;

/**
 * Intermediate exception grouping all Book-related errors (Hierarchy Level 2).
 * <p>
 * Extends {@link BaseAutoBookException} and overrides {@link #getErrorCode()}
 * to return the category-level code {@code "BOOK"}. Concrete book exceptions
 * extend this class and further override {@link #getErrorCode()} with a
 * more specific code (Level 3).
 * </p>
 */
public class BookException extends BaseAutoBookException {

    /**
     * Constructs a new BookException with the given message.
     *
     * @param message human-readable error description
     */
    public BookException(String message) {
        super(message);
    }

    /**
     * Returns the category-level error code for all book-related errors.
     * Overridden at Level 3 by concrete subclasses for more specific codes.
     *
     * @return {@code "BOOK"}
     */
    @Override
    public String getErrorCode() {
        return "BOOK";
    }
}
