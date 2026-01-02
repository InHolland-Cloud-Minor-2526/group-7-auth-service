package org.acme.dbHandler;

import org.acme.auth.dto.RegistrationDTO;
import org.acme.auth.dto.RegistrationResponseDTO;
import org.acme.auth.security.JwtUtil;
import org.acme.clients.UserServiceClient;
import org.acme.entity.User;
import org.eclipse.microprofile.rest.client.inject.RestClient;

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

    @Inject
    @RestClient
    UserServiceClient userServiceClient;

    public Long findUser(String email, String password) {
        try {
            Long userId = em.createQuery(
                    "SELECT u FROM User u WHERE u.email = :email AND u.password = :password",
                    User.class)
                    .setParameter("email", email)
                    .setParameter("password", password)
                    .getSingleResult().userId;

            return userId;

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
        insertTokens(user, accessToken, refreshToken);
        userServiceClient.createDefaultProfile(userId);
      
        return new RegistrationResponseDTO(accessToken, refreshToken);

    }

    private boolean isUserExist(String email) {
        Long count = em.createQuery(
                "SELECT COUNT(u) FROM User u WHERE u.email = :email",
                Long.class)
                .setParameter("email", email)
                .getSingleResult();
        return count > 0;
    }

    private void insertTokens(User user, String accessToken, String refreshToken) {
        user.accessToken = BcryptUtil.bcryptHash(accessToken);
        user.refreshToken = BcryptUtil.bcryptHash(refreshToken);
        em.flush();

    }

    public void updateToken(Long userId, String token) {
        int updated = em.createQuery(
                "UPDATE User u SET u.token = :token WHERE u.userId = :userId")
                .setParameter("token", token)
                .setParameter("userId", userId)
                .executeUpdate();

        System.out.println(updated);

        if (updated == 0) {
            throw new NoResultException("Error with login, try again please");
        }
    }
}
