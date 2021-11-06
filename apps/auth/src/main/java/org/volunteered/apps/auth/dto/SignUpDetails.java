package org.volunteered.apps.auth.dto;

public class SignUpDetails {
    private String username;
    private String password;
    private String confirmPassword;

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
