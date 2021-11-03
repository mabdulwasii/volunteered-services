package org.volunteered.apps.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.volunteered.apps.auth.repository.UserRepository;
import org.volunteered.apps.auth.service.AuthService;

@RestController
public class LoginController {

    private final AuthService authService;

    public LoginController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
    }

    @PostMapping({"/login/{username}/{password}"})
    public ResponseEntity<String> login(@PathVariable String username, @PathVariable String password) {
        String token = authService.authenticate(username, password);
        return ResponseEntity.ok(token);
    }
}