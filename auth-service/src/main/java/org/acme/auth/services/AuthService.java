package org.acme.auth.services;

import org.acme.auth.customExceptions.FailedToUpdateTokens;
import org.acme.auth.customExceptions.InvalidCredential;
import org.acme.auth.customExceptions.RegistrationFailedException;
import org.acme.auth.customExceptions.UserExistsException;
import org.acme.auth.dto.LoginRequest;
import org.acme.auth.dto.LoginResponse;
import org.acme.auth.dto.RegistrationDTO;
import org.acme.auth.dto.RegistrationResponseDTO;
import org.acme.auth.entity.User;
import org.acme.auth.repository.AuthHandler;
import org.acme.auth.utils.JwtUtil;
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
    @Inject
    AccessTokenService tokenCacheService;
    @Inject
    JwtUtil jwtUtil;

    public User validateUser(LoginRequest request) {
        User user = authHandler.findUserByEmail(request.email);
        if (user == null || !BcryptUtil.matches(request.password, user.password)) {
            throw new InvalidCredential();
        }
        return user;
    }

    public LoginResponse generateTokens(LoginRequest request) {
        try {
            User user = validateUser(request);

            String accessToken = jwtUtil.generateAccessToken(user.userId);
            String refreshToken = jwtUtil.generateRefreshToken(user.userId);
            // check if the access token already exists in redis if so delete it an store the new one
            //
            tokenCacheService.storeAccessToken(
                    user.userId,
                    accessToken,
                    jwtUtil.getAccessTokenTtlSeconds()
            );
            authHandler.updateRefreshToken(
                    user.userId,
                    BcryptUtil.bcryptHash(refreshToken)
            );

            return new LoginResponse(accessToken, refreshToken);
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
            // Store access token in Redis (TTL = JWT expiry)
            tokenCacheService.storeAccessToken(
                    registrationResponseDTO.userId,
                    registrationResponseDTO.accessToken,
                    jwtUtil.getAccessTokenTtlSeconds()
            );

            userEventPublisher.publishUserRegistered(registrationResponseDTO.userId);
            return registrationResponseDTO;

        } catch (Exception e) {
            throw new RegistrationFailedException();
        }
    }

    public String getNewAccessTokenWithRefreshToken(String refreshToken) {
        return authHandler.getNewAccessTokenWithRefreshToken(refreshToken);
    }
}
