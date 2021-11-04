package org.volunteered.apps.auth.service;

import org.volunteered.apps.auth.dto.Jwt;
import org.volunteered.apps.auth.dto.LoginDetails;

public interface AuthService {
    Jwt authenticate(LoginDetails loginDetails);
}
