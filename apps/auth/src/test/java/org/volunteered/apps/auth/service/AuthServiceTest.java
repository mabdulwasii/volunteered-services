package org.volunteered.apps.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.volunteered.apps.auth.AuthApplication;
import org.volunteered.apps.auth.config.RSAKeyConfigProperties;
import org.volunteered.apps.auth.dto.Jwt;
import org.volunteered.apps.auth.dto.LoginDetails;
import org.volunteered.apps.auth.dto.SignUpDetails;
import org.volunteered.apps.auth.model.User;
import org.volunteered.apps.auth.security.jwt.JWTUtils;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@WithMockUser
@SpringBootTest(classes = AuthApplication.class)
class AuthServiceTest {

    public static final String ACCESS_TOKEN = "$%^&&***";
    public static final String REFRESH_TOKEN = "436%%#&*#373883";
    public static final long USER_ID = 1L;
    public static final String username = "admin@example.com";
    public static final String PASSWORD = "admin";

    @MockBean
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
    @DisplayName("should Authenticate registered user")
    void shouldAuthenticateRegisteredUser() throws Exception {

        var toBeReturned = new Jwt(ACCESS_TOKEN, REFRESH_TOKEN, 1L, username, new ArrayList<>());
        var loginDetails = new LoginDetails(username, PASSWORD);

        var jwt = authService.authenticate(loginDetails);
        assertNull(jwt);
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
    @DisplayName("should Register new user")
    void shouldThrowNew() throws Exception {

        var signUpDetails = new SignUpDetails(username, PASSWORD, PASSWORD);
        var user = new User(USER_ID, signUpDetails.getUsername(), signUpDetails.getPassword(), true);

        when(userService.createUser(signUpDetails)).thenReturn(Optional.of(user));

        var apiResponse = authService.register(signUpDetails);

        assertNotNull(apiResponse);
        assertEquals(apiResponse.getMessage(), "User created successfully");
    }


}