package org.volunteered.apps.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.volunteered.apps.auth.dto.ApiResponse;
import org.volunteered.apps.auth.dto.Jwt;
import org.volunteered.apps.auth.dto.LoginDetails;
import org.volunteered.apps.auth.dto.RefreshTokenRequest;
import org.volunteered.apps.auth.dto.RefreshTokenResponse;
import org.volunteered.apps.auth.dto.SignUpDetails;
import org.volunteered.apps.auth.repository.UserRepository;
import org.volunteered.apps.auth.service.AuthService;

@RestController
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    public AuthController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @PostMapping({"/signup"})
    public ResponseEntity<?> signup(@RequestBody SignUpDetails userInfo) {
        if (userRepository.existsByUsername(userInfo.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse("Error: Username taken. Please input another username"));
        }

        var register = authService.register(userInfo);
        return ResponseEntity.ok().body(register);
    }

    @PostMapping({"/signin"})
    public ResponseEntity<?> signin(@RequestBody LoginDetails loginDetails) throws Exception {
        Jwt token = authService.authenticate(loginDetails);
        return ResponseEntity.ok(token);
    }

    @PostMapping({"/refresh_token"})
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        RefreshTokenResponse refreshTokenResponse = authService.refreshToken(refreshTokenRequest);
        return ResponseEntity.ok(refreshTokenResponse);
    }

}