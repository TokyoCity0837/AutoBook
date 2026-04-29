package com.autobook.Exception;

/**
 * Abstract base exception for all AutoBook domain exceptions.
 * <p>
 * Forms the root of the AutoBook exception hierarchy (Hierarchy 1, Level 1).
 * Every domain-specific exception must provide a unique machine-readable error code
 * via {@link #getErrorCode()}, enabling uniform error handling and logging across
 * the entire application.
 * </p>
 *
 * @see BookException
 * @see UserException
 */
public abstract class BaseAutoBookException extends RuntimeException {

    /**
     * Constructs a new exception with the given detail message.
     *
     * @param message human-readable description of the error
     */
    protected BaseAutoBookException(String message) {
        super(message);
    }

    /**
     * Returns a machine-readable error code uniquely identifying this error category.
     * <p>
     * Each subclass MUST override this method -- this is the core polymorphic
     * behaviour of the exception hierarchy (Overriding, Level 1 → Level 2 → Level 3).
     * </p>
     *
     * @return error code string, e.g. {@code "BOOK"} or {@code "BOOK_NOT_FOUND"}
     */
    public abstract String getErrorCode();
}
