package org.acme.auth.dto;

public class RegistrationResponseDTO {
    public String accessToken;
    public String refreshToken;
    public Long userId;

    public RegistrationResponseDTO(String accessToken, String refreshToken, Long userId) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.userId = userId;
    }
}
