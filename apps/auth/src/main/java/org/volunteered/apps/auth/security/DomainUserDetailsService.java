package org.volunteered.apps.auth.security;

import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.volunteered.apps.auth.domain.User;
import org.volunteered.apps.auth.repository.UserRepository;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Authenticate a user from the database.
 */
@Component("userDetailsService")
public class DomainUserDetailsService implements UserDetailsService {

    private final Logger log = LoggerFactory.getLogger(DomainUserDetailsService.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String username) {
        log.debug("Authenticating {}", username);

        if (new EmailValidator().isValid(username, null)) {
            return userRepository.findOneWithAuthoritiesByEmailIgnoreCase(username)
                    .map(user -> createSpringSecurityUser(username, user))
                    .orElseThrow(() -> new UsernameNotFoundException("User with email " + username + " was not found in the database"));
        }

        String lowerUsername = username.toLowerCase(Locale.ENGLISH);
        return userRepository.findOneWithAuthoritiesByUsername(lowerUsername)
                .map(user -> createSpringSecurityUser(lowerUsername, user))
                .orElseThrow(() -> new UsernameNotFoundException("User " + lowerUsername + " was not found in the database"));

    }

    private org.springframework.security.core.userdetails.User createSpringSecurityUser(String username, User user) {
        if (!user.isStatus()) {
            throw new UserNotActivatedException("User " + username + " was not activated");
        }
        List<GrantedAuthority> grantedAuthorities = user.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getName()))
                .collect(Collectors.toList());
        return new org.springframework.security.core.userdetails.User(user.getUsername(),
                user.getPassword(),
                grantedAuthorities);
    }
}
