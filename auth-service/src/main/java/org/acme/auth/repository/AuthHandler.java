package org.acme.auth.repository;

import java.util.List;

import org.acme.auth.dto.RegistrationDTO;
import org.acme.auth.dto.RegistrationResponseDTO;
import org.acme.auth.entity.User;
import org.acme.auth.utils.JwtUtil;

import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class AuthHandler {

    @Inject
    EntityManager em;
    @Inject
    JwtUtil jwtUtil;

    @Transactional
    public RegistrationResponseDTO registerUser(RegistrationDTO registrationDTO) {

        User user = new User();
        String password = registrationDTO.password;
        user.email = registrationDTO.email;
        user.password = BcryptUtil.bcryptHash(password);
        em.persist(user);
        em.flush();
        RegistrationResponseDTO registrationResponseDTO = saveTokens(user);
        return registrationResponseDTO;

    }

    private RegistrationResponseDTO saveTokens(User user) {
        String accessToken = jwtUtil.generateAccessToken(user.userId);
        String refreshToken = jwtUtil.generateRefreshToken(user.userId);
        user.hashed_access_token = BcryptUtil.bcryptHash(accessToken);
        user.hashed_refresh_token = BcryptUtil.bcryptHash(refreshToken);
        return new RegistrationResponseDTO(accessToken, refreshToken, user.userId);
    }

    @Transactional
    public void updateTokens(Long userId, String hashedAccessToken, String hashedRefreshToken) {
        User user = em.find(User.class, userId);
        if (user == null) {
            throw new NoResultException("User not found");
        }
        user.hashed_access_token = hashedAccessToken;
        user.hashed_refresh_token = hashedRefreshToken;
        em.flush();
    }

    public User findUserByEmail(String email) {
        try {
            return em.createQuery(
                    "SELECT u FROM User u WHERE u.email = :email",
                    User.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public String getNewAccessTokenWithRefreshToken(String refreshToken) {
        User user = getUserByRefreshToken(refreshToken);
        if (user == null) {
            throw new NoResultException("User not found");
        }

        String newAccessToken = jwtUtil.generateAccessToken(user.userId);
        String hashedNewAccessToken = BcryptUtil.bcryptHash(newAccessToken);
        updateAccessToken(user.userId, hashedNewAccessToken);
        return newAccessToken;
    }

    private User getUserByRefreshToken(String refreshToken) {
        List<User> users = em.createQuery(
                "SELECT u FROM User u WHERE u.hashed_refresh_token IS NOT NULL",
                User.class
        ).getResultList();

        for (User user : users) {
            if (BcryptUtil.matches(refreshToken, user.hashed_refresh_token)) {
                return user;
            }
        }

        return null;
    }

    @Transactional
    public void updateAccessToken(Long userId, String hashedAccessToken) {
        User user = em.find(User.class, userId);
        if (user == null) {
            throw new NoResultException("User not found");
        }
        user.hashed_access_token = hashedAccessToken;
    }
}
