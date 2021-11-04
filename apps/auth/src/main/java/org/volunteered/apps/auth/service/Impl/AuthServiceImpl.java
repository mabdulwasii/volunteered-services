package org.volunteered.apps.auth.service.Impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.volunteered.apps.auth.config.RSAKeyConfigProperties;
import org.volunteered.apps.auth.dto.Jwt;
import org.volunteered.apps.auth.security.jwt.JWTUtils;
import org.volunteered.apps.auth.security.service.UserDetailsImpl;
import org.volunteered.apps.auth.service.AuthService;

import java.security.PrivateKey;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final AuthenticationManager authenticationManager;

    private final JWTUtils jwtUtils;

    private final RSAKeyConfigProperties rsaKeyProp;

    public AuthServiceImpl(AuthenticationManager authenticationManager, JWTUtils jwtUtils, RSAKeyConfigProperties rsaKeyProp) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.rsaKeyProp = rsaKeyProp;
    }

    public Jwt authenticate(String username, String password) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (BadCredentialsException e) {
            e.printStackTrace();
            throw new RuntimeException("Bad Credentials");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("User authentication failed");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl loginUser = (UserDetailsImpl) authentication.getPrincipal();
        // Generating Token
        PrivateKey privateKey = rsaKeyProp.getPrivateKey();
        String jwt = jwtUtils.generateToken(loginUser, privateKey);

        List<String> authorities = loginUser.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new Jwt(jwt, loginUser.getId(), loginUser.getUsername(), authorities);
    }
}