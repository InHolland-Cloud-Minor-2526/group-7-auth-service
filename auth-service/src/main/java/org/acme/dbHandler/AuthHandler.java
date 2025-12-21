package org.acme.dbHandler;

import org.acme.entity.User;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class AuthHandler {

    @Inject
    EntityManager em;

    // Find user by email and password
    public User findUser(String email, String password) {
        try {
            User user = em.createQuery(
                    "SELECT u FROM User u WHERE u.email = :email AND u.password = :password",
                    User.class)
                    .setParameter("email", email)
                    .setParameter("password", password)
                    .getSingleResult();

            return user;

        } catch (NoResultException e) {
            // TODO do the normal exception handling
            System.out.println(e.getMessage());
            return null;
        }
    }

    // Update refresh token for a user
    @Transactional
    public void updateRefreshToken(User user, String refreshToken) {
        user.token = refreshToken;
        em.merge(user);
    }

    // Find user by refresh token
    public User findUserByRefreshToken(String refreshToken) {
        try {
            return em.createQuery(
                    "SELECT u FROM User u WHERE u.token = :token",
                    User.class)
                    .setParameter("token", refreshToken)
                    .getSingleResult();
        } catch (NoResultException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
