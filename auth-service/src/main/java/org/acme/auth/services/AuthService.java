package org.acme.auth.services;

import jakarta.inject.Inject;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.auth.dto.LoginRequest;
import org.acme.dbHandler.AuthHandler;
import org.acme.customExceptions.CustomAuthException;

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
}
