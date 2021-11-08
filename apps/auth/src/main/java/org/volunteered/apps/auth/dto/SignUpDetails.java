package org.volunteered.apps.auth.dto;

import javax.validation.constraints.Email;

public class SignUpDetails {

    @Email(message = "Invalid email")
    private final String username;
    private final String password;
    private final String confirmPassword;

    public SignUpDetails(@Email(message = "Invalid email") String username, String password, String confirmPassword) {
        this.username = username;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    public String getPassword() {
        return password;
    }

    public String getConfirmPassword() {
        return this.confirmPassword;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", confirmPassword='" + confirmPassword + '\'' +
                '}';
    }
}
