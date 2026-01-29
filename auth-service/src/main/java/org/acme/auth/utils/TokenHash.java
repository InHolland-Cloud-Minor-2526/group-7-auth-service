package org.acme.auth.utils;
import java.security.MessageDigest;
import java.util.Base64;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TokenHash {

    public String sha256(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(value.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
