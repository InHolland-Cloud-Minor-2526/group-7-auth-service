package org.acme.auth.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.persistence.EntityManager;

import org.acme.dbHandler.AuthHandler;
import org.acme.entity.PasswordResetToken;
import org.acme.entity.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

@ApplicationScoped
public class PasswordResetService {

    private static final SecureRandom RNG = new SecureRandom();
    private static final Duration RESET_TTL = Duration.ofMinutes(15);

    @Inject AuthHandler authHandler;
    @Inject EntityManager em;

    public static class IssueResult {
        public final String rawToken;
        public final String email;

        public IssueResult(String rawToken, String email) {
            this.rawToken = rawToken;
            this.email = email;
        }
    }

    @Transactional
    public IssueResult issueResetTokenByEmail(String email) {
        User user = authHandler.findUserByEmail(email);
        if (user == null) return null;

        String raw = generateRandomToken();
        String hash = sha256Hex(raw);

        PasswordResetToken prt = new PasswordResetToken();
        prt.setEmail(email);
        prt.setTokenHash(hash);
        prt.setExpiresAt(Instant.now().plus(RESET_TTL));
        prt.setUsed(false);

        em.persist(prt);

        return new IssueResult(raw, email);
    }

    private static String generateRandomToken() {
        byte[] bytes = new byte[48];
        RNG.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static String sha256Hex(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(digest.length * 2);
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException("Hash error", e);
        }
    }

    @Transactional
    public String consumeValidTokenAndGetEmail(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) return null;

        String hash = sha256Hex(rawToken);

        PasswordResetToken prt; 
        try {
            prt = em.createQuery(
                "SELECT p FROM PasswordResetToken p WHERE p.tokenHash = :hash",
                PasswordResetToken.class
            ).setParameter("hash", hash).getSingleResult();
        } catch (jakarta.persistence.NoResultException e) {
            return null;
        }

        // Validate token
        if (prt.isUsed()) return null;
        if (prt.getExpiresAt().isBefore(Instant.now())) return null;

        // Mark as used (one-time token)
        prt.setUsed(true);
        em.merge(prt);

        return prt.getEmail();
    }
}
