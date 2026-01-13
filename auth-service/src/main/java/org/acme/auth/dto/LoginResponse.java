package org.acme.auth.dto;

public class LoginResponse {
    public String token;
    public Long user_id;

    public LoginResponse(String token, Long user_id) {
        this.token = token;
        this.user_id = user_id;
    }
}
