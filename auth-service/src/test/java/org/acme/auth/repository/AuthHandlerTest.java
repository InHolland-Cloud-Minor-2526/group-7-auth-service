package org.acme.auth.repository;

import org.acme.auth.dto.RegistrationDTO;
import org.acme.auth.dto.RegistrationResponseDTO;
import org.acme.auth.entity.User;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

@QuarkusTest
class AuthHandlerTest {

    @Inject
    AuthHandler authHandler;

    @Inject
    EntityManager em;


    @Test
    void registerUser_persistsUserAndReturnsTokens() {
        RegistrationDTO dto = new RegistrationDTO();
        dto.email = "repo@mail.com";
        dto.password = "123456";

        RegistrationResponseDTO response =
                authHandler.registerUser(dto);

        assertNotNull(response);
        assertNotNull(response.accessToken);
        assertNotNull(response.refreshToken);
        assertNotNull(response.userId);

        User user = em.find(User.class, response.userId);
        assertNotNull(user);
        assertEquals(dto.email, user.email);
        assertNotNull(user.hashed_access_token);
        assertNotNull(user.hashed_refresh_token);
    }


    @Test
    void findUserByEmail_returnsUser() {
        RegistrationDTO dto = new RegistrationDTO();
        dto.email = "find@mail.com";
        dto.password = "123456";

        authHandler.registerUser(dto);

        User user = authHandler.findUserByEmail("find@mail.com");

        assertNotNull(user);
        assertEquals("find@mail.com", user.email);
    }


    @Test
    void updateTokens_updatesSuccessfully() {
        RegistrationDTO dto = new RegistrationDTO();
        dto.email = "update@mail.com";
        dto.password = "123456";

        RegistrationResponseDTO response =
                authHandler.registerUser(dto);

        authHandler.updateTokens(
                response.userId,
                "hashed-access",
                "hashed-refresh"
        );

        User user = em.find(User.class, response.userId);
        assertEquals("hashed-access", user.hashed_access_token);
        assertEquals("hashed-refresh", user.hashed_refresh_token);
    }


    @Test
    void getNewAccessTokenWithRefreshToken_returnsNewToken() {
        RegistrationDTO dto = new RegistrationDTO();
        dto.email = "refresh@mail.com";
        dto.password = "123456";

        RegistrationResponseDTO response =
                authHandler.registerUser(dto);

        String newAccessToken =
                authHandler.getNewAccessTokenWithRefreshToken(
                        response.refreshToken
                );

        assertNotNull(newAccessToken);
    }


    @Test
    void updateAccessToken_userNotFound_throwsException() {
        assertThrows(
                NoResultException.class,
                () -> authHandler.updateAccessToken(999L, "token")
        );
    }
}
