package org.volunteered.apps.auth.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.volunteered.apps.auth.config.RSAKeyConfigProperties;
import org.volunteered.apps.auth.security.jwt.JWTToken;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JWTToken jwtToken;

    @Autowired
    private RSAKeyConfigProperties rsaKeyProp;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        String token = resolveToken(httpServletRequest);

        if (StringUtils.hasText(token)) {
            String username = jwtToken.getUsernameFromToken(token, rsaKeyProp.getPublicKey());

            logger.info("Public Key ===> " + rsaKeyProp.getPublicKey());

            // If the user information can be correctly extracted from JWT, and the user is not authorized
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtToken.validateToken(token, userDetails, rsaKeyProp.getPublicKey())) {
                    // Authorize the user using the JWT token
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    // It will be handed over to spring security management, and it will not be intercepted for secondary authorization in subsequent filters
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(jwtToken.getHeader());
        String parameterToken = request.getParameter("token");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        if (StringUtils.hasText(parameterToken)) {
            return parameterToken;
        }
        return null;
    }
}