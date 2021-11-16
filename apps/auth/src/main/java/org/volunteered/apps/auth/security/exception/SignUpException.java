package org.volunteered.apps.auth.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * This exception is thrown in case of a not activated user trying to authenticate.
 */
public class SignUpException extends AuthenticationException {

    private static final long serialVersionUID = 1L;

    public SignUpException(String message) {
        super(message);
    }
}
