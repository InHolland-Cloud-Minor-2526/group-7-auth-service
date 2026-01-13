package org.acme.auth.services;

import org.acme.auth.dto.LoginRequest;
import org.acme.auth.dto.RegistrationDTO;
import org.acme.auth.dto.RegistrationResponseDTO;
import org.acme.customExceptions.CustomAuthException;
import org.acme.dbHandler.AuthHandler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AuthService {

    @Inject
    AuthHandler authHandler;

    public Long validateUser(LoginRequest request) throws CustomAuthException {
        if (request.email == null || request.password == null) {
            throw new CustomAuthException("Password and Email should not be empty");
        }

        Long userId = authHandler.findUser(request.email, request.password);

        if (userId == null) {
            throw new CustomAuthException("User was not found");
        }

        return userId;
    }

    public void updateToken(Long userId, String token) throws CustomAuthException {
        try {
            authHandler.updateToken(userId, token);
        } catch (Exception e) {
            throw new CustomAuthException(e.getMessage());
        }
    }

    public RegistrationResponseDTO createUser(RegistrationDTO registrationDTO) {
        return authHandler.createUser(registrationDTO);
    }

}
