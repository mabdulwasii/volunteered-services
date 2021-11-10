package org.volunteered.apps.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.volunteered.apps.auth.AuthApplication;
import org.volunteered.apps.auth.config.RSAKeyConfigProperties;
import org.volunteered.apps.auth.dto.RefreshTokenRequest;
import org.volunteered.apps.auth.dto.SignUpDetails;
import org.volunteered.apps.auth.dto.TokenRefreshResponse;
import org.volunteered.apps.auth.model.RefreshToken;
import org.volunteered.apps.auth.model.User;
import org.volunteered.apps.auth.security.exception.SignUpException;
import org.volunteered.apps.auth.security.exception.TokenRefreshExpiredException;
import org.volunteered.apps.auth.security.jwt.JWTUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WithMockUser
@SpringBootTest(classes = AuthApplication.class)
class AuthServiceTest {

    public static final String ACCESS_TOKEN = "$%^&&***";
    public static final String REFRESH_TOKEN = "436%%#&*#373883";
    public static final long USER_ID = 1L;
    public static final String username = "admin@example.com";
    public static final String PASSWORD = "admin";

    @Autowired
    private AuthService authService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private RefreshTokenService refreshTokenService;

    @MockBean
    private UserService userService;


    @MockBean
    private JWTUtils jwtUtils;

    @MockBean
    private RSAKeyConfigProperties rsaKeyProp;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("should Register new user")
    void shouldRegisterNewUser() throws Exception {

        var signUpDetails = new SignUpDetails(username, PASSWORD, PASSWORD);
        var user = new User(USER_ID, signUpDetails.getUsername(), signUpDetails.getPassword(), true);

        when(userService.createUser(signUpDetails)).thenReturn(Optional.of(user));

        var apiResponse = authService.register(signUpDetails);

        assertNotNull(apiResponse);
        assertEquals(apiResponse.getMessage(), "User created successfully");
    }

    @Test
    @DisplayName("should Throw exception if creation failed")
    void shouldThrowExceptionIfUserNotSaved() {

        var signUpDetails = new SignUpDetails(username, PASSWORD, PASSWORD);
        var user = new User(USER_ID, signUpDetails.getUsername(), signUpDetails.getPassword(), true);

        when(userService.createUser(signUpDetails)).thenReturn(Optional.empty());

        assertThrows(SignUpException.class, () -> authService.register(signUpDetails));
    }

    @Test
    @DisplayName("should Refresh token if token has not expired")
    void shouldRefreshTokenIfNotExpired() {

        var user = new User(USER_ID, username, PASSWORD, true);
        var refreshToken = new RefreshToken(1L, user, REFRESH_TOKEN, Instant.now().plus(Duration.ofMillis(2000)));

        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(REFRESH_TOKEN);

        when(refreshTokenService.findByToken(REFRESH_TOKEN)).thenReturn(Optional.of(refreshToken));
        when(refreshTokenService.verifyExpiration(any())).thenReturn(refreshToken);
        when(jwtUtils.generateToken(any(), any())).thenReturn(ACCESS_TOKEN);


        TokenRefreshResponse tokenRefreshResponse = authService.refreshToken(refreshTokenRequest);

        assertNotNull(tokenRefreshResponse);
        assertEquals(tokenRefreshResponse.getRefreshToken(), REFRESH_TOKEN);
        assertEquals(tokenRefreshResponse.getTokenType(), "Bearer");
        assertEquals(tokenRefreshResponse.getAccessToken(), ACCESS_TOKEN);
    }

    @Test
    @DisplayName("should throw exception if token has expired")
    void shouldThrowExceptionIfTokenExpired() {

        var user = new User(USER_ID, username, PASSWORD, true);
        var refreshToken = new RefreshToken(1L, user, REFRESH_TOKEN, Instant.now().plus(Duration.ofMillis(2000)));

        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(REFRESH_TOKEN);

        when(refreshTokenService.findByToken(REFRESH_TOKEN)).thenReturn(Optional.of(refreshToken));
        when(refreshTokenService.verifyExpiration(any())).thenThrow(TokenRefreshExpiredException.class);
        when(jwtUtils.generateToken(any(), any())).thenReturn(ACCESS_TOKEN);

        assertThrows(TokenRefreshExpiredException.class, () -> authService.refreshToken(refreshTokenRequest));
    }


}