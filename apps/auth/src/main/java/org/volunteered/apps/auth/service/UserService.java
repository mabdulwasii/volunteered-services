package org.volunteered.apps.auth.service;

import org.volunteered.apps.auth.dto.SignUpDetails;
import org.volunteered.apps.auth.model.User;

import java.util.Optional;

public interface UserService {

    Optional<User> createUser(SignUpDetails userInfo);

}
