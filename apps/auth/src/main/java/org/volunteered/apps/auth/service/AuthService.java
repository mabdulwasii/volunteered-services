package org.volunteered.apps.auth.service;

import org.volunteered.apps.auth.dto.*;

public interface AuthService {

    Jwt authenticate(LoginDetails loginDetails) throws Exception;

    TokenRefreshResponse refreshToken(RefreshTokenRequest refreshTokenRequest);

    ApiResponse register(SignUpDetails userInfo);
}
