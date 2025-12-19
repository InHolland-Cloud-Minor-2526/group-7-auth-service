package org.acme.auth.services;

import org.acme.auth.dto.LoginRequest;
import org.acme.auth.dto.RegistrationDTO;
import org.acme.dbHandler.AuthHandler;
import org.acme.entity.User;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AuthService {

    @Inject
    AuthHandler authHandler;

    public User validateUser(LoginRequest request) {
        if (request.email == null || request.password == null) {
            throw new IllegalArgumentException("Something is null");
        }

        User user = authHandler.findUser(request.email, request.password);

        if (user == null) {
            throw new IllegalArgumentException("User was not found");
        }

        return user;

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
