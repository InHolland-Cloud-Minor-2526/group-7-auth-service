package org.acme.dbHandler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;

import org.acme.entity.User;

@ApplicationScoped
public class AuthHandler {

    @Inject
    EntityManager em;

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
