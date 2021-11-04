package org.volunteered.apps.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.volunteered.apps.auth.dto.Jwt;
import org.volunteered.apps.auth.dto.LoginDetails;
import org.volunteered.apps.auth.service.AuthService;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping({"/login"})
    public ResponseEntity<?> login(@RequestBody LoginDetails loginDetails) {
        Jwt token = authService.authenticate(loginDetails);
        return ResponseEntity.ok(token);
    }
}