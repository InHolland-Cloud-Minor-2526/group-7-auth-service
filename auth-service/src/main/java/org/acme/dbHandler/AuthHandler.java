package org.acme.dbHandler;

import org.acme.auth.dto.RegistrationDTO;
import org.acme.auth.dto.RegistrationResponseDTO;
import org.acme.auth.security.JwtUtil;
import org.acme.entity.User;

import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;

@ApplicationScoped
public class AuthHandler {

    @Inject
    EntityManager em;
    @Inject
    JwtUtil jwtUtil;

    public User findUser(String email) {
        try {
            User user = em.createQuery(
                    "SELECT u FROM User u WHERE u.email = :email",
                    User.class)
                    .setParameter("email", email)
                    .getSingleResult();
            return user;
        } catch (NoResultException e) {
            return null;
        }
    }

    @Transactional
    public RegistrationResponseDTO createUser(RegistrationDTO registrationDTO) {

        if (isUserExist(registrationDTO.email)) {
            throw new WebApplicationException("User already registered", 409);
        }
        User user = new User();

        String password = registrationDTO.password;
        user.email = registrationDTO.email;
        user.password = BcryptUtil.bcryptHash(password);
        em.persist(user);
        em.flush();
        Long userId = user.userId;

        String accessToken = jwtUtil.generateAccessToken(userId);
        String refreshToken = jwtUtil.generateRefreshToken(userId);
        RegistrationResponseDTO response = insertTokens(user, accessToken, refreshToken);
        return response;

    }

    private boolean isUserExist(String email) {
        Long count = em.createQuery(
                "SELECT COUNT(u) FROM User u WHERE u.email = :email",
                Long.class)
                .setParameter("email", email)
                .getSingleResult();
        return count > 0;
    }

    private RegistrationResponseDTO insertTokens(User user, String accessToken, String refreshToken) {
        user.access_token = BcryptUtil.bcryptHash(accessToken);
        user.refresh_token = BcryptUtil.bcryptHash(refreshToken);
        em.flush();
        return new RegistrationResponseDTO(accessToken, refreshToken, user.userId);
    }

    @Transactional
    public void updateToken(Long userId, String accessToken, String refreshToken) {
        int updated = em.createQuery(
                "UPDATE User u SET u.access_token = :accessToken, u.refresh_token = :refresh_token WHERE u.user_id = :userId")
                .setParameter("accessToken", accessToken)
                .setParameter("refreshToken", refreshToken)
                .setParameter("userId", userId)
                .executeUpdate();

        if (updated == 0) {
            throw new NoResultException("Error with login, try again please");
        }
    }

    public String getNewAccessTokenWithRefreshToken(String refreshToken) {
        try {
            User user = em.createQuery(
                    "SELECT u FROM User u WHERE u.refresh_token = :refreshToken",
                    User.class)
                    .setParameter("refreshToken", refreshToken)
                    .getSingleResult();

            String newAccessToken = jwtUtil.generateAccessToken(user.userId);
            updateToken(user.userId, newAccessToken, refreshToken);
            return newAccessToken;
        } catch (NoResultException e) {
            throw new WebApplicationException("Invalid refresh token", 401);
        }
    }
}
