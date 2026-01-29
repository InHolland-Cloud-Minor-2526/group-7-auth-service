package org.acme.auth.security;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Duration;
import java.util.Set;

@ApplicationScoped
public class JwtUtil {
    public String generateToken(String email) {
        return Jwt.issuer("expense-auth-service")
                .upn(email)
                .groups(Set.of("USER"))
                .expiresIn(Duration.ofHours(2))
                .sign();
    }
}
