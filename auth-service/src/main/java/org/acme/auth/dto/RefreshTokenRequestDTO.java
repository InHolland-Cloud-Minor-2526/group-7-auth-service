package org.acme.auth.dto;

import jakarta.validation.constraints.NotBlank;

public class RefreshTokenRequestDTO {

    @NotBlank(message = "Refresh token is required")
    public String refreshToken;
}
