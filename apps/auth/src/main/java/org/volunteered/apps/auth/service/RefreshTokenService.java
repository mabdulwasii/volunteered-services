package org.volunteered.apps.auth.service;

import org.volunteered.apps.auth.model.RefreshToken;

import java.util.Optional;

public interface RefreshTokenService {

    Optional<RefreshToken> createRefreshToken(Long id);

    Optional<RefreshToken> findByToken(String refreshToken);

    RefreshToken verifyExpiration(RefreshToken refreshToken);
}
