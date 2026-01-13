package org.acme.auth.services;

import org.acme.auth.dto.LoginRequest;
import org.acme.auth.dto.RegistrationDTO;
import org.acme.auth.dto.RegistrationResponseDTO;
import org.acme.customExceptions.CustomAuthException;
import org.acme.dbHandler.AuthHandler;
import org.acme.entity.User;
import org.acme.messaging.UserEventPublisher;

import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AuthService {

    @Inject
    AuthHandler authHandler;
    @Inject
    UserEventPublisher userEventPublisher;

    public Long validateUser(LoginRequest request) throws CustomAuthException {

        if (request.email == null || request.password == null) {
            throw new CustomAuthException("Password and Email should not be empty");
        }

        User user = authHandler.findUser(request.email);

        if (user.userId == null || !BcryptUtil.matches(request.password, user.password)) {
            throw new CustomAuthException("Invalid credentials");
        }

        return user.userId;
    }

    public void updateToken(Long userId, String accessToken, String refreshToken) throws CustomAuthException {
        try {
            authHandler.updateToken(userId, accessToken, refreshToken);
        } catch (Exception e) {
            throw new CustomAuthException(e.getMessage());
        }
    }

    public RegistrationResponseDTO createUser(RegistrationDTO dto) {
        RegistrationResponseDTO response = authHandler.createUser(dto);
        userEventPublisher.publishUserRegistered(response.userId);
        return response;
    }

}
