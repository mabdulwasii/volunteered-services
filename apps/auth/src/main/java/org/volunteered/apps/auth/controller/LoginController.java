package org.volunteered.apps.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.volunteered.apps.auth.repository.UserRepository;
import org.volunteered.apps.auth.service.AuthService;

@RestController
public class LoginController {

    @Autowired
    private AuthService authService;
    @Autowired
    private UserRepository userRepository;

    @PostMapping({"/login/{username}/{password}"})
    public ResponseEntity<String> login(@PathVariable String username, @PathVariable String password) {
        String token = authService.authenticate(username, password);
        return ResponseEntity.ok(token);
    }
}