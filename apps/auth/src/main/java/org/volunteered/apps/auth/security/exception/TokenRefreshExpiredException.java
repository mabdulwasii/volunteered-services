package org.volunteered.apps.auth.security.exception;

public class TokenRefreshExpiredException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TokenRefreshExpiredException(String token, String message) {
        super(String.format("Failed for [%s]: %s", token, message));
    }
}
