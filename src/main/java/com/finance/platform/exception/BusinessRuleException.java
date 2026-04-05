package com.finance.platform.exception;

/**
 * Thrown when a business rule is violated.
 * Maps to HTTP 422 Unprocessable Entity.
 *
 * Examples:
 * - Attempting to modify a soft-deleted record
 * - An INACTIVE user trying to perform an action
 *
 * This is distinct from validation errors (400 Bad Request).
 * Validation errors mean the input FORMAT is wrong.
 * Business rule violations mean the input is well-formed but
 * the OPERATION is not allowed given the current system state.
 */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
