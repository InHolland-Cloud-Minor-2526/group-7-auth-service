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
    @Transactional
    public void updatePasswordByEmail(String email, String newPassword) {
        em.createQuery("UPDATE User u SET u.password = :password WHERE u.email = :email")
            .setParameter("password", newPassword)
            .setParameter("email", email)
            .executeUpdate();
    }
}
