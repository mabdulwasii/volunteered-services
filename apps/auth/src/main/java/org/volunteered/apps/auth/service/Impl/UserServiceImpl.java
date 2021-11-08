package org.volunteered.apps.auth.service.Impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.volunteered.apps.auth.dto.SignUpDetails;
import org.volunteered.apps.auth.model.Authority;
import org.volunteered.apps.auth.model.User;
import org.volunteered.apps.auth.model.enumeration.AuthorityType;
import org.volunteered.apps.auth.repository.AuthorityRepository;
import org.volunteered.apps.auth.repository.UserRepository;
import org.volunteered.apps.auth.service.UserService;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthorityRepository authorityRepository;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthorityRepository authorityRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authorityRepository = authorityRepository;
    }

    @Override
    public Optional<User> createUser(SignUpDetails signUpDetails) {
        User savedUser = null;

        if (signUpDetails.getPassword().equalsIgnoreCase(signUpDetails.getConfirmPassword())) {

            String encryptedPassword = passwordEncoder.encode(signUpDetails.getPassword());
            var user = new User(signUpDetails.getUsername(), encryptedPassword);

            Set<Authority> authorities = new HashSet<>();
            authorityRepository.findByName(AuthorityType.ROLE_USER).ifPresent(authorities::add);
            user.setAuthorities(authorities);

            System.out.println("User Authorities ===> " + authorities);

            user.setActivated(true);

            System.out.println("user ===> " + user);


            savedUser = userRepository.save(user);

            System.out.println("Saved user ===> " + savedUser);

        }

        return Optional.ofNullable(savedUser);

    }
}
