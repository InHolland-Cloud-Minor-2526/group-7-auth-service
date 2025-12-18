package org.acme.dbHandler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

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
}
