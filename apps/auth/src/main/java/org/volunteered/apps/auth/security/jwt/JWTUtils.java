package org.volunteered.apps.auth.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;

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
     * Get the user name from the token
     *
     * @param token token
     * @return user name
     */
    public Optional<String> getUsernameFromToken(String token, PublicKey publicKey) {
        String username;
        try {
            var optionalClaimsFromToken = getClaimsFromToken(token, publicKey);

            username = optionalClaimsFromToken.map(Claims::getSubject).orElse(null);
        } catch (Exception e) {
            username = null;
        }
        return Optional.ofNullable(username);
    }

    /**
     * Determine whether the token has expired
     *
     * @param token token
     * @return Is it overdue
     */
    public Boolean isTokenExpired(String token, PublicKey publicKey) {
        try {
            var optionalClaimsFromToken = getClaimsFromToken(token, publicKey);

            if (optionalClaimsFromToken.isPresent()) {
                var expiration = optionalClaimsFromToken.get().getExpiration();
                return expiration.before(new Date());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    /**
     * refresh token
     *
     * @param token Original token
     * @return New token
     */
    public String refreshToken(String token, PublicKey publicKey, PrivateKey privateKey) {
        String refreshedToken;
        try {
            var optionalClaimsFromToken = getClaimsFromToken(token, publicKey);
            refreshedToken = optionalClaimsFromToken.map(claims -> generateToken(claims, privateKey)).orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
            refreshedToken = null;
        }
        return refreshedToken;
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

    public String getHeader() {
        return header;
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
