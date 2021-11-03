package org.volunteered.apps.auth.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;

@Component
public class JWTToken {


    @Value("${token.header}")
    private String header;

    //@Value("${token.secret}")
    //private String secret;

    @Value("${token.expiration}")
    private long expiration;

    private static String createJTI() {
        return new String(Base64.getEncoder().encode(UUID.randomUUID().toString().getBytes()));
    }

    /**
     * Generate token token
     *
     * @param userDetails user
     * @return token token
     */
    public String generateToken(UserDetails userDetails, PrivateKey privateKey) {
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
    public String getUsernameFromToken(String token, PublicKey publicKey) {
        String username;
        try {
            Claims claims = getClaimsFromToken(token, publicKey);
            username = claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    /**
     * Determine whether the token has expired
     *
     * @param token token
     * @return Is it overdue
     */
    public Boolean isTokenExpired(String token, PublicKey publicKey) {
        try {
            Claims claims = getClaimsFromToken(token, publicKey);
            Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            return false;
        }
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
            Claims claims = getClaimsFromToken(token, publicKey);
            refreshedToken = generateToken(claims, privateKey);
        } catch (Exception e) {
            refreshedToken = null;
        }
        return refreshedToken;
    }

    /**
     * Authentication token
     *
     * @param token       token
     * @param userDetails user
     * @return Is it effective
     */
    public Boolean validateToken(String token, UserDetails userDetails, PublicKey publicKey) {
        String username = getUsernameFromToken(token, publicKey);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token, publicKey));
    }

    /**
     * Generate token
     *
     * @param claims Data declaration
     * @return token token
     */
    private String generateToken(Map<String, Object> claims, PrivateKey privateKey) {
        Date date = new Date(System.currentTimeMillis());
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
    private Claims getClaimsFromToken(String token, PublicKey publicKey) {
        Claims claims;
        try {
            claims = Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }

    public String getHeader() {
        return header;
    }


    @Override
    public String toString() {
        return "JWTToken{" +
                "header='" + header + '\'' +
                ", expiration=" + expiration +
                '}';
    }
}
