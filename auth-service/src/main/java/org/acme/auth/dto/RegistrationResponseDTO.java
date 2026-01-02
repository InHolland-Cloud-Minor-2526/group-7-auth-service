package org.acme.auth.dto;

public class RegistrationResponseDTO {
    public String accessToken;
    public String refreshToken;

    public RegistrationResponseDTO(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
