package org.volunteered.apps.service.exception;

/**
 * This exception is thrown in case of a not activated user trying to authenticate.
 */
public class UserAlreadyExistException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UserAlreadyExistException(String message) {
        super(message);
    }
}
