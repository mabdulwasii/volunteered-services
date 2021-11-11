package org.volunteered.apps.auth.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.volunteered.apps.auth.AuthApplication;
import org.volunteered.apps.auth.dto.SignUpDetails;
import org.volunteered.apps.auth.model.Authority;
import org.volunteered.apps.auth.model.User;
import org.volunteered.apps.auth.model.enumeration.AuthorityType;
import org.volunteered.apps.auth.repository.AuthorityRepository;
import org.volunteered.apps.auth.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = AuthApplication.class)
@ExtendWith(SpringExtension.class)
@WithMockUser
class UserServiceTest {
	
	public static final long USER_ID = 3L;
    public static final String username = "dola@example.com";
    public static final String PASSWORD = "admin";
    public static final String ENCRYPTED_PASSWORD = "^&**#GEHE&**(((";

    private final UserRepository userRepository = mock(UserRepository.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final AuthorityRepository authorityRepository = mock(AuthorityRepository.class);
    @Autowired
    private UserService userService;
	
	@Test
    @DisplayName("Should not create user if passwords mismatch")
    void shouldNotCreateUserIfPasswordMisMatch() {
        var signUpDetails = new SignUpDetails(username, PASSWORD, "383838");

        var OptionalUser = userService.createUser(signUpDetails);

        assertFalse(OptionalUser.isPresent(), "User created successfully");

    }

    @Test
    @DisplayName("Should create user if passwords match")
    void shouldCreateUserIfPasswordMatch() {

        var user = new User(USER_ID, username, PASSWORD, true);
        var role_user = new Authority(10, AuthorityType.ROLE_USER);
        var signUpDetails = new SignUpDetails(username, PASSWORD, PASSWORD);

        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn(ENCRYPTED_PASSWORD);
        when(authorityRepository.findByName(any())).thenReturn(Optional.of(role_user));
        when(userRepository.save(any())).thenReturn(user);

        var OptionalUser = userService.createUser(signUpDetails);

        assertTrue(OptionalUser.isPresent(), "User not created");
        assertEquals(OptionalUser.get().getUsername(), username);
        assertTrue(OptionalUser.get().isActivated());

    }
}