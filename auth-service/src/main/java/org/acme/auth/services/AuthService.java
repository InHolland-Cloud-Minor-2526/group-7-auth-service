package org.acme.auth.services;

import org.acme.auth.dto.LoginRequest;
import org.acme.auth.security.JwtUtil;
import org.acme.dbHandler.AuthHandler;
import org.acme.entity.User;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AuthService {

    @Inject
    AuthHandler authHandler;

    @Inject
    JwtUtil jwtUtil;

    public User validateUser(LoginRequest request) {
        if (request == null || request.email == null || request.password == null) {
            throw new IllegalArgumentException("Something is null");
        }

        User user = authHandler.findUser(request.email, request.password);

        if (user == null) {
            throw new IllegalArgumentException("User was not found!");
        }

        return user;

    }

    public String issueRefreshToken(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User context missing");
        }
        // Generate a new refresh token
        //String refreshToken = UUID.randomUUID().toString();
        String refreshToken = jwtUtil.generateRefreshToken(user.email);
        // Store the refresh token associated with the user
        authHandler.updateRefreshToken(user, refreshToken);

        return refreshToken;
    }

    // Validate the refresh token and return the associated user
    public User validateRefreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException("Refresh token is null");
        }
        // Find user by refresh token
        User user = authHandler.findUserByRefreshToken(refreshToken);

        if (user == null) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        return user;
    }
}
