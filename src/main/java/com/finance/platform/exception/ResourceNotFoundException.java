package com.finance.platform.exception;

/**
 * Thrown when a requested resource does not exist.
 *
 * Maps to HTTP 404. Using a custom exception rather than returning
 * null from services because:
 * 1. Null propagation is the #1 source of NullPointerExceptions
 * 2. The exception carries a meaningful message for the API response
 * 3. The global exception handler converts it to a structured error
 * 4. It makes the "not found" case EXPLICIT in the service contract
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
