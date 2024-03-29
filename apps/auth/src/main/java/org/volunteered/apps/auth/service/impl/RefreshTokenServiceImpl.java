package org.volunteered.apps.auth.service.impl;

import org.springframework.stereotype.Service;
import org.volunteered.apps.auth.model.RefreshToken;
import org.volunteered.apps.auth.repository.RefreshTokenRepository;
import org.volunteered.apps.auth.repository.UserRepository;
import org.volunteered.apps.auth.security.exception.TokenRefreshExpiredException;
import org.volunteered.apps.auth.security.jwt.JWTUtils;
import org.volunteered.apps.auth.service.RefreshTokenService;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JWTUtils jwtUtils;

    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository, JWTUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Optional<RefreshToken> createRefreshToken(Long userId) {
	    RefreshToken refreshToken = null;

	    var optionalUser = userRepository.findById(userId);

	    if (optionalUser.isPresent()) {
		    refreshToken = new RefreshToken();
		    refreshToken.setUser(optionalUser.get());
		    refreshToken.setExpiryDate(Instant.now().plusMillis(jwtUtils.getRefreshExpiration()));
		    refreshToken.setToken(UUID.randomUUID().toString());

		    refreshToken = refreshTokenRepository.save(refreshToken);
	    }

	    return Optional.ofNullable(refreshToken);
    }

    @Override
    public Optional<RefreshToken> findByToken(String refreshToken) {
        return refreshTokenRepository.findByToken(refreshToken);
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken refreshToken) {
        if (refreshToken.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(refreshToken);
            throw new TokenRefreshExpiredException(refreshToken.getToken(), "Refresh token was expired. Please make a new sign in request");
        }

        return refreshToken;
    }
}
