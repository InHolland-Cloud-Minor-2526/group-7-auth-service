package org.acme.auth.services;

import jakarta.inject.Inject;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.auth.dto.LoginRequest;
import org.acme.dbHandler.AuthHandler;

import org.acme.entity.User;

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

    public void updatePassword(String email, String newPassword) {
        authHandler.updatePasswordByEmail(email, newPassword);
    }
}
