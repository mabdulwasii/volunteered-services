package org.volunteered.apps.auth.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.volunteered.apps.auth.AuthApplication;
import org.volunteered.apps.auth.dto.*;
import org.volunteered.apps.auth.repository.UserRepository;
import org.volunteered.apps.auth.service.AuthService;

import java.util.ArrayList;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = AuthApplication.class)
@AutoConfigureMockMvc
@WithMockUser
class AuthControllerTest {

    public static final String ACCESS_TOKEN = "$%^&&***";
    public static final String REFRESH_TOKEN = "436%%#&*#373883";
    public static final long USER_ID = 1L;
    public static final String username = "admin@example.com";
    public static final String PASSWORD = "admin";

    @MockBean
    private AuthService authService;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {

    }

    @Test
    @DisplayName("Should signup if username does not exist")
    void shouldSignupWithValidUsername() throws Exception {

        var signUpDetails = new SignUpDetails("example@gmail.com", "1234567890", "1234567890");
        var apiResponse = new ApiResponse("User created successfully");

        when(authService.register(signUpDetails)).thenReturn(apiResponse);

        //execute the post request
        mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(signUpDetails)))
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("Should not signup if username exist")
    void shouldNotSignupIfUsernameExists() throws Exception {

        var signUpDetails = new SignUpDetails(username, "1234567890", "1234567890");

        var apiResponse = new ApiResponse("Error: Username taken. Please input another username");

        when(userRepository.existsByUsername(username)).thenReturn(true);

        //execute the post request
        mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(signUpDetails)))

                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(asJsonString(apiResponse)))
                .andExpect(jsonPath("$.message", is("Error: Username taken. Please input another username")));
    }

    @Test
    @DisplayName("Should not signup if email is invalid")
    void shouldNotSignupIfEmailIsInvalid() {

        var signUpDetails = new SignUpDetails("try.com", "1234567890", "1234567890");

        when(authService.register(signUpDetails)).thenThrow(RuntimeException.class);

        //execute the post request

        Assertions.assertThrows(AssertionError.class, () -> {
            mockMvc.perform(post("/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(signUpDetails)))

                    .andExpect(status().is5xxServerError())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        });
    }

    @Test
    @DisplayName("Should sign in registered user")
    void shouldSignInIfRegisteredUser() throws Exception {

        var loginDetails = new LoginDetails(username, PASSWORD);
        var jwt = new Jwt(ACCESS_TOKEN, REFRESH_TOKEN, USER_ID, username, new ArrayList<>());

        when(authService.authenticate(loginDetails)).thenReturn(jwt);

        mockMvc.perform(post("/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(loginDetails)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should refresh token if token is valid")
    void shouldRefreshTokenIfCodeIsValid() throws Exception {

        var refreshTokenRequest = new RefreshTokenRequest(REFRESH_TOKEN);
        var tokenRefreshResponse = new TokenRefreshResponse(ACCESS_TOKEN, REFRESH_TOKEN);

        when(authService.refreshToken(any())).thenReturn(tokenRefreshResponse);

        mockMvc.perform(post("/refresh_token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(refreshTokenRequest)))
                .andExpect(status().isOk());

    }


    private String asJsonString(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }
}