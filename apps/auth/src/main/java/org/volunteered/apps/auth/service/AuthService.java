package org.volunteered.apps.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.volunteered.apps.auth.config.RSAKeyConfigProperties;
import org.volunteered.apps.auth.security.jwt.JWTToken;

import java.security.PrivateKey;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);


    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTToken jwtToken;

    @Autowired
    private RSAKeyConfigProperties rsaKeyProp;

    public String authenticate(String username, String password) {
        // User authentication
        Authentication authentication = null;
        try {
            // This method will call UserDetailsServiceImpl.loadUserByUsername'
            System.out.println("username ===> " + username);
            System.out.println("password ===> " + password);

            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            System.out.println("Auth ===> " + authentication.toString());
        } catch (BadCredentialsException e) {
            e.printStackTrace();
            throw new RuntimeException("Bad Credentials");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("User authentication failed");
        }
        UserDetails loginUser = (UserDetails) authentication.getPrincipal();
        // Generating Token
        PrivateKey privateKey = rsaKeyProp.getPrivateKey();
        logger.info("privateKey for generate ===> " + privateKey + "END");
        return jwtToken.generateToken(loginUser, privateKey);
    }
}