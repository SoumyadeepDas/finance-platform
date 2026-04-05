package com.finance.platform.exception;

/**
 * Thrown when a create or update violates a uniqueness constraint.
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
