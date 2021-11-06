package org.volunteered.apps.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.volunteered.apps.auth.dto.*;
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
        return ResponseEntity.ok(authService.register(userInfo));
    }

    @PostMapping({"/signin"})
    public ResponseEntity<?> signin(@RequestBody LoginDetails loginDetails) {

        Jwt token = authService.authenticate(loginDetails);
        return ResponseEntity.ok(token);
    }

    @PostMapping({"/refresh_token"})
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        TokenRefreshResponse tokenRefreshResponse = authService.refreshToken(refreshTokenRequest);
        return ResponseEntity.ok(tokenRefreshResponse);
    }

}