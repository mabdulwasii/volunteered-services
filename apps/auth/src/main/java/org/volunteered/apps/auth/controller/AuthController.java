package org.volunteered.apps.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.volunteered.apps.auth.service.AuthService;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping({"/login/{username}/{password}"})
    public ResponseEntity<String> login(@PathVariable String username, @PathVariable String password) {
        String token = authService.authenticate(username, password);
        return ResponseEntity.ok(token);
    }
}