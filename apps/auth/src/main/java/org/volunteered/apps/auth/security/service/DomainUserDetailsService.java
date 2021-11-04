package org.volunteered.apps.auth.security.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.volunteered.apps.auth.domain.User;
import org.volunteered.apps.auth.repository.UserRepository;
import org.volunteered.apps.auth.security.UserNotActivatedException;

/**
 * Authenticate a user from the database.
 */
@Component("userDetailsService")
public class DomainUserDetailsService implements UserDetailsService {

    private final Logger log = LoggerFactory.getLogger(DomainUserDetailsService.class);

    private final UserRepository userRepository;

    public DomainUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetailsImpl loadUserByUsername(final String username) {
        log.debug("Authenticating {}", username);

        User loginUser = userRepository.findOneWithAuthoritiesByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User " + username + " was not found in the database"));

        if (!loginUser.isStatus()) {
            throw new UserNotActivatedException("User " + username + " was not activated");
        }
        return UserDetailsImpl.build(loginUser);
    }

}
