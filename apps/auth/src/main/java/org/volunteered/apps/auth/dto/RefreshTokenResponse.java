package org.volunteered.apps.auth.dto;

public class RefreshTokenResponse{
	
	private final String accessToken;
	private final String refreshToken;
	private final String tokenType = "Bearer";
	
	public RefreshTokenResponse(String accessToken, String refreshToken) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}
	
	public String getAccessToken() {
		return accessToken;
	}

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }
	
	@Override
    public String toString() {
        return "JwtRefreshToken{" +
                "accessToken='" + accessToken + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", tokenType='" + tokenType + '\'' +
                '}';
    }
}