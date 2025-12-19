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

    @Transactional
    public User createUser(String email, String password) {
        boolean userExist = em.createQuery(
                "SELECT COUNT(u) FROM User u WHERE u.email = :email",
                Long.class)
                .setParameter("email", email)
                .getSingleResult() > 0;

        if (userExist) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.email = email;
        user.password = password;
        user.token = null;

        em.persist(user);
        return user;
    }

}
