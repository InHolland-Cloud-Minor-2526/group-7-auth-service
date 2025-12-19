package org.acme.auth.services;

import org.acme.auth.dto.LoginRequest;
import org.acme.auth.dto.RegistrationDTO;
import org.acme.dbHandler.AuthHandler;
import org.acme.entity.User;
import org.acme.customExceptions.CustomAuthException;

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

    public User createUser(RegistrationDTO registrationDTO) {
        if (registrationDTO.email == null || registrationDTO.password == null) {
            throw new IllegalArgumentException("Please enter both email and password to register");
        }

        User user = authHandler.createUser(registrationDTO.email, registrationDTO.password);

        if (user == null) {
            throw new IllegalArgumentException("User creation failed"); 
        }

        return user;
    }

}
