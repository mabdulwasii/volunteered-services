package org.volunteered.apps.auth.service;

import org.volunteered.apps.auth.dto.ApiResponse;
import org.volunteered.apps.auth.dto.Jwt;
import org.volunteered.apps.auth.dto.LoginDetails;
import org.volunteered.apps.auth.dto.RefreshTokenRequest;
import org.volunteered.apps.auth.dto.RefreshTokenResponse;
import org.volunteered.apps.auth.dto.SignUpDetails;

public interface AuthService {
    Jwt authenticate(LoginDetails loginDetails) throws Exception;
    RefreshTokenResponse refreshToken(RefreshTokenRequest refreshTokenRequest);
    ApiResponse register(SignUpDetails userInfo);
}
