package org.volunteered.apps.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.volunteered.apps.auth.AuthApplication;
import org.volunteered.apps.auth.model.RefreshToken;
import org.volunteered.apps.auth.model.User;
import org.volunteered.apps.auth.repository.RefreshTokenRepository;
import org.volunteered.apps.auth.repository.UserRepository;
import org.volunteered.apps.auth.security.exception.TokenRefreshExpiredException;
import org.volunteered.apps.auth.security.jwt.JWTUtils;
import org.volunteered.apps.auth.service.Impl.RefreshTokenServiceImpl;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = AuthApplication.class)
class RefreshTokenServiceTest {

    public static final String REFRESH_TOKEN = "436%%#&*#373883";
    public static final long USER_ID = 3L;
    public static final String username = "dola@example.com";
    public static final String PASSWORD = "admin";


    private final RefreshTokenRepository refreshTokenRepository = mock(RefreshTokenRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final JWTUtils jwtUtils = mock(JWTUtils.class);


    private RefreshTokenService refreshTokenService;

    @BeforeEach
    void setup() {
        refreshTokenService = new RefreshTokenServiceImpl(refreshTokenRepository, userRepository, jwtUtils);
    }


    @Test
    @DisplayName("Should create refresh token from user Id")
    void shouldCreateRefreshTokenUsingUserId() {

        var user = new User(USER_ID, username, PASSWORD, true);
        var refreshToken = new RefreshToken(1L, user, REFRESH_TOKEN, Instant.now());


        when(userRepository.save(any())).thenReturn(user);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(refreshTokenRepository.save(any())).thenReturn(refreshToken);
        when(jwtUtils.getRefreshExpiration()).thenReturn(10000L);

        var refreshTokenReturned = refreshTokenService.createRefreshToken(USER_ID);


        assertTrue(refreshTokenReturned.isPresent(), "RefreshToken not returned");
        assertEquals(refreshTokenReturned.get(), refreshToken);
        assertEquals(refreshTokenReturned.get().getToken(), refreshToken.getToken());
        assertEquals(refreshTokenReturned.get().getUser(), refreshToken.getUser());


    }

    @Test
    @DisplayName("Should not create refresh token with Invalid User id")
    void shouldNotCreateRefreshTokenWithInvalidUserId() {

        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        var refreshTokenReturned = refreshTokenService.createRefreshToken(USER_ID);

        assertFalse(refreshTokenReturned.isPresent(), "RefreshToken not returned");

    }

    @Test
    @DisplayName("Should retrieve refresh token by token")
    void shouldFindByToken() {

        var user = new User(USER_ID, username, PASSWORD, true);
        var refreshToken = new RefreshToken(1L, user, REFRESH_TOKEN, Instant.now());

        when(refreshTokenRepository.findByToken(REFRESH_TOKEN)).thenReturn(Optional.of(refreshToken));

        var refreshTokenOptional = refreshTokenService.findByToken(REFRESH_TOKEN);

        assertTrue(refreshTokenOptional.isPresent(), "RefreshToken not returned");
        assertSame(refreshTokenOptional.get(), refreshToken);
        assertSame(refreshTokenOptional.get().getUser(), user);
        assertEquals(refreshTokenOptional.get().getToken(), refreshToken.getToken());
        assertEquals(refreshTokenOptional.get().getExpiryDate(), refreshToken.getExpiryDate());

    }

    @Test
    @DisplayName("Should Verify refresh token has expired")
    void shouldVerifyTokenExpired() {

        var user = new User(USER_ID, username, PASSWORD, true);
        var refreshToken = new RefreshToken(1L, user, REFRESH_TOKEN, Instant.now().minus(Duration.ofDays(1)));

        assertThrows(TokenRefreshExpiredException.class, () -> {
            refreshTokenService.verifyExpiration(refreshToken);
        });


    }

    @Test
    @DisplayName("Should verify refresh token has not expired")
    void shouldVerifyHasNotTokenExpired() {

        var user = new User(USER_ID, username, PASSWORD, true);
        var refreshToken = new RefreshToken(1L, user, REFRESH_TOKEN, Instant.now().plus(Duration.ofMillis(2000)));

        RefreshToken refreshTokenReturned = refreshTokenService.verifyExpiration(refreshToken);

        assertEquals(refreshTokenReturned, refreshToken);
        assertEquals(refreshTokenReturned.getId(), refreshToken.getId());
        assertEquals(refreshTokenReturned.getToken(), refreshToken.getToken());
        assertEquals(refreshTokenReturned.getUser(), refreshToken.getUser());
        assertEquals(refreshTokenReturned.getExpiryDate(), refreshToken.getExpiryDate());


    }


}