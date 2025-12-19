package org.acme.auth.dto;

public class RegistrationResponseDTO {
    public String token;

    public RegistrationResponseDTO(String token) {
        this.token = token;
    }
}
