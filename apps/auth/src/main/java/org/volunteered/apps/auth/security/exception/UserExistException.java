package org.volunteered.apps.auth.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * This exception is thrown in case of a not activated user trying to authenticate.
 */
public class UserExistException extends AuthenticationException {

    private static final long serialVersionUID = 1L;

    public UserExistException(String message) {
        super(message);
    }
}
