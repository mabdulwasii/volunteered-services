package org.volunteered.apps.auth.security.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.volunteered.apps.auth.AuthApplication;
import org.volunteered.apps.auth.model.User;
import org.volunteered.apps.auth.repository.UserRepository;
import org.volunteered.apps.auth.security.exception.UserNotActivatedException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@SpringBootTest(classes = AuthApplication.class)
class DomainUserDetailsServiceTest {

    public static final String ACCESS_TOKEN = "$%^&&***";
    public static final String REFRESH_TOKEN = "436%%#&*#373883";
    public static final long USER_ID = 3L;
    public static final String username = "dola@example.com";
    public static final String PASSWORD = "admin";
    public static final String ENCRYPTED_PASSWORD = "^&**#GEHE&**(((";

    private final UserRepository userRepository = mock(UserRepository.class);

    private DomainUserDetailsService domainUserDetailsService;

    @BeforeEach
    void setUp() {

        domainUserDetailsService = new DomainUserDetailsService(userRepository);
    }

    @Test
    @DisplayName("Should load user if username is found")
    void shouldLoadUserByUsername() {
        var user = new User(USER_ID, username, PASSWORD, true);

        when(userRepository.findOneWithAuthoritiesByUsernameIgnoreCase(username)).thenReturn(Optional.of(user));

        var userDetails = domainUserDetailsService.loadUserByUsername(username);

        assertNotNull(userDetails);
        assertEquals(userDetails.getUsername(), user.getUsername());
        assertEquals(userDetails.getId(), user.getId());
        assertEquals(userDetails.getPassword(), user.getPassword());


    }

    @Test
    @DisplayName("Should not load user if username is is not found")
    void shouldThrowExceptionIfUsernameNotFound() {

        when(userRepository.findOneWithAuthoritiesByUsernameIgnoreCase(username)).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> domainUserDetailsService.loadUserByUsername(username));

    }

    @Test
    @DisplayName("Should not load user if username is is not found")
    void shouldThrowExceptionIfUsernameNotActivated() {

        var user = new User(USER_ID, username, PASSWORD, false);

        when(userRepository.findOneWithAuthoritiesByUsernameIgnoreCase(username)).thenReturn(Optional.of(user));
        assertThrows(UserNotActivatedException.class, () -> domainUserDetailsService.loadUserByUsername(username));

    }
}