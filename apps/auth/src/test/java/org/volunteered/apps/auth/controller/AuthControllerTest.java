package org.volunteered.apps.auth.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.volunteered.apps.auth.config.RSAKeyConfigProperties;
import org.volunteered.apps.auth.dto.ApiResponse;
import org.volunteered.apps.auth.dto.SignUpDetails;
import org.volunteered.apps.auth.service.AuthService;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    private final AuthService authService = mock(AuthService.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RSAKeyConfigProperties rsaKeyConfigProperties;

    @BeforeEach
    void setUp() throws Exception {
        rsaKeyConfigProperties.loadRSAKey();
    }


    @Test
    @DisplayName("Should signup if username does not exist")
    void shouldSignupWithValidUsername() throws Exception {

        var signUpDetails = new SignUpDetails("example@gmail.com", "1234567890", "1234567890");
        var apiResponse = new ApiResponse("User created successfully");

        doReturn(apiResponse).when(authService.register(signUpDetails));

        //execute the post request
        mockMvc.perform(post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(signUpDetails)))

                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.is("User created successfully")));


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