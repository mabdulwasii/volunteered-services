package org.volunteered.apps.auth.service.Impl;

import org.springframework.stereotype.Service;
import org.volunteered.apps.auth.dto.SignUpDetails;
import org.volunteered.apps.auth.model.User;
import org.volunteered.apps.auth.repository.UserRepository;
import org.volunteered.apps.auth.service.UserService;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> createUser(SignUpDetails signUpDetails) {
        User savedUser = null;

        if (signUpDetails.getPassword().equalsIgnoreCase(signUpDetails.getConfirmPassword())) {
            var user = new User(signUpDetails.getUsername(), signUpDetails.getPassword());

            savedUser = userRepository.save(user);
        }

        return Optional.ofNullable(savedUser);
    }
}
