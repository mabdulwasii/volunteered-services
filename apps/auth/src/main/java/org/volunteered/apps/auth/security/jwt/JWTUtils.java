package org.volunteered.apps.auth.security.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JWTUtils {

    @Value("${token.header}")
    private String header;

    @Value("${token.expiration}")
    private long expiration;

    @Value("${token.refreshExpiration}")
    private long refreshExpiration;

    private static String createJTI() {
        return new String(Base64.getEncoder().encode(UUID.randomUUID().toString().getBytes()));
    }

    /**
     * Generate token token
     *
     * @param userDetails user
     * @return token token
     */
    public String generateToken(UserDetailsImpl userDetails, PrivateKey privateKey) {
	    HashMap<String, Object> claims = new HashMap<>();
	    claims.put("sub", userDetails.getUsername());
	    return generateToken(claims, privateKey);
    }

	/**
	 * Generate token
	 *
	 * @param claims Data declaration
	 * @return token token
	 */
	private String generateToken(Map<String, Object> claims, PrivateKey privateKey) {
		Date date = new Date();
		return Jwts.builder().setClaims(claims)
				.setId(createJTI())
				.setIssuedAt(date)
                .setExpiration(new Date(date.getTime() + expiration))
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .compact();
    }

    /**
     * Get data claim
     *
     * @param token token
     * @return Data declaration
     */
    private Optional<Claims> getClaimsFromToken(String token, PublicKey publicKey) {
        Claims claims;
        try {
            claims = Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            claims = null;
        }
        return Optional.ofNullable(claims);
    }

    public long getRefreshExpiration() {
        return refreshExpiration;
    }

    @Override
    public String toString() {
        return "JWTToken{" +
                "header='" + header + '\'' +
                ", expiration=" + expiration +
                '}';
    }
}
