package org.volunteered.apps.auth.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.volunteered.apps.auth.config.RSAKeyConfigProperties;
import org.volunteered.apps.auth.dto.ApiResponse;
import org.volunteered.apps.auth.dto.Jwt;
import org.volunteered.apps.auth.dto.LoginDetails;
import org.volunteered.apps.auth.dto.RefreshTokenRequest;
import org.volunteered.apps.auth.dto.RefreshTokenResponse;
import org.volunteered.apps.auth.dto.SignUpDetails;
import org.volunteered.apps.auth.model.RefreshToken;
import org.volunteered.apps.auth.security.exception.SignUpException;
import org.volunteered.apps.auth.security.exception.TokenRefreshExpiredException;
import org.volunteered.apps.auth.security.jwt.JWTUtils;
import org.volunteered.apps.auth.security.jwt.UserDetailsImpl;
import org.volunteered.apps.auth.service.AuthService;
import org.volunteered.apps.auth.service.RefreshTokenService;
import org.volunteered.apps.auth.service.UserService;

import java.security.PrivateKey;
import java.util.List;
import java.util.stream.Collectors;

import static org.volunteered.apps.auth.security.jwt.UserDetailsImpl.build;

@Service
public class AuthServiceImpl implements AuthService {
	private final AuthenticationManager authenticationManager;
	private final RefreshTokenService refreshTokenService;
	private final UserService userService;
	private final JWTUtils jwtUtils;
	private final RSAKeyConfigProperties rsaKeyProp;

	public AuthServiceImpl(AuthenticationManager authenticationManager,
	                       RefreshTokenService refreshTokenService,
	                       UserService userService,
	                       JWTUtils jwtUtils,
	                       RSAKeyConfigProperties rsaKeyProp) {
		this.authenticationManager = authenticationManager;
		this.refreshTokenService = refreshTokenService;
		this.userService = userService;
		this.jwtUtils = jwtUtils;
		this.rsaKeyProp = rsaKeyProp;
	}

	public Jwt authenticate(LoginDetails loginDetails) throws Exception {
		Authentication authentication;

		try {
			authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginDetails.getUsername(), loginDetails.getPassword()));
		} catch (BadCredentialsException e) {
			e.printStackTrace();
			throw new BadCredentialsException("Bad Credentials");
		} catch (Exception e) {
			e.printStackTrace();
            throw new Exception("User authentication failed");
        }

        // Generating Token
        PrivateKey privateKey = rsaKeyProp.getPrivateKey();

        if (authentication != null) {
            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetailsImpl loginUser = (UserDetailsImpl) authentication.getPrincipal();
            String jwt = jwtUtils.generateToken(loginUser, privateKey);

            List<String> authorities = loginUser.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

	        var optionalRefreshToken = refreshTokenService.createRefreshToken(loginUser.getId());

	        String refreshToken = optionalRefreshToken.map(RefreshToken::getToken).orElse("");

	        return new Jwt(jwt, refreshToken, loginUser.getId(), loginUser.getUsername(), authorities);
        } else {
	        throw new RuntimeException("Invalid Login Details");
        }
	}

	public RefreshTokenResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
		String requestRefreshToken = refreshTokenRequest.getRefreshToken();

		return refreshTokenService.findByToken(requestRefreshToken)
				.map(refreshTokenService::verifyExpiration).map(RefreshToken::getUser)
				.map(user -> {
					String token = null;
					try {
						token = jwtUtils.generateToken(build(user), rsaKeyProp.getPrivateKey());
					} catch (Exception e) {
						e.printStackTrace();
					}
					return new RefreshTokenResponse(token, requestRefreshToken);
				})
		        .orElseThrow(() -> new TokenRefreshExpiredException(requestRefreshToken,
				        "Error: Invalid refresh token!"));
    }

    @Override
    public ApiResponse register(SignUpDetails signUpDetails) {

        var userOptional = userService.createUser(signUpDetails);

        return userOptional
		        .map(user -> new ApiResponse("User created successfully"))
		        .orElseThrow(() -> new SignUpException("Error: User creation failed"));
    }
}
