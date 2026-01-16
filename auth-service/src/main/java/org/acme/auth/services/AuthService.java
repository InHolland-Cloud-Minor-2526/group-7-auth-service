package org.acme.auth.services;

import org.acme.auth.customExceptions.FailedToUpdateTokens;
import org.acme.auth.customExceptions.InvalidCredential;
import org.acme.auth.customExceptions.RegistrationFailedException;
import org.acme.auth.customExceptions.UserExistsException;
import org.acme.auth.dto.LoginRequest;
import org.acme.auth.dto.RegistrationDTO;
import org.acme.auth.dto.RegistrationResponseDTO;
import org.acme.auth.entity.User;
import org.acme.auth.repository.AuthHandler;
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

    public User validateUser(LoginRequest request) {
        User user = authHandler.findUserByEmail(request.email);
        if (user == null || !BcryptUtil.matches(request.password, user.password)) {
            throw new InvalidCredential();
        }
        return user;
    }

    public void updateToken(Long userId, String hashedAccessToken, String hashedRefreshToken) {
        try {
            authHandler.updateTokens(userId, hashedAccessToken, hashedRefreshToken);
        } catch (Exception e) {
            throw new FailedToUpdateTokens();
        }
    }

    public RegistrationResponseDTO registerUser(RegistrationDTO dto) {

        if (authHandler.findUserByEmail(dto.email) != null) {
            throw new UserExistsException();
        }

        try {
            RegistrationResponseDTO registrationResponseDTO = authHandler.registerUser(dto);
            userEventPublisher.publishUserRegistered(registrationResponseDTO.userId);
            return registrationResponseDTO;

        } catch (Exception e) {
            throw new RegistrationFailedException();
        }
    }

    public String getNewAccessTokenWithRefreshToken(String refreshToken) {
        // try {
            return authHandler.getNewAccessTokenWithRefreshToken(refreshToken);
        // } catch (Exception e) {
        //     throw new NewAccessTokenGeneration();
        // }
    }
}
