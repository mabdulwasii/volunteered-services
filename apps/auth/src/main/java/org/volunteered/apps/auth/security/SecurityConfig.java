package org.volunteered.apps.auth.security;

import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.volunteered.apps.auth.security.service.DomainUserDetailsService;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final DomainUserDetailsService userDetailsService;

    public SecurityConfig(DomainUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Solution unable to inject authentication manager directly
     *
     * @return PasswordEncoder
     * @throws Exception
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // token based distributed authentication, so no session is required
        http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // CRSF is disabled. Because the session is not used, cross site csrf attack defense is disabled. Otherwise, the login cannot succeed
                .csrf().disable()
                // Configure permissions
                .authorizeRequests()
                // Login login CaptchaImage allows anonymous access
                .antMatchers("/signin").permitAll()
                .antMatchers("/signup").permitAll()
                .antMatchers("/refresh_token").permitAll()
                .antMatchers("/h2-console/**").permitAll()
                // Except for all the above requests, authentication is required
                .anyRequest().authenticated()
                .and()
                // Allows cross domain access, equivalent to corsConfigurationSource of config class
                .cors()
                .and();

        // Exit function
        http.logout().logoutUrl("/logout");

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }
}
