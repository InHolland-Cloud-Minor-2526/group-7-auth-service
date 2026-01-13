package org.acme.auth.dto;

public class LoginResponse {
    public String accessToken;
    public String refreshToken;

    public LoginResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
