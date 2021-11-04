package org.volunteered.apps.auth.service;

import org.volunteered.apps.auth.dto.Jwt;

public interface AuthService {
    Jwt authenticate(String username, String password);
}
