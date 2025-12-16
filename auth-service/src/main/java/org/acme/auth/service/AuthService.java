package org.acme.auth.service;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.Map;

@ApplicationScoped
public class AuthService {
    private static final Map<String, String> USERS = Map.of(
            "nguyenhieu@gmail.com", "hieu1234",
            "phandung@gmail.com", "dung4321",
            "ducnguyen@gmail.com", "duc2004"
    );

    public boolean authenticate(String email, String password) {
        return USERS.containsKey(email)
                && USERS.get(email).equals(password);
    }
}
