package org.acme.auth.security;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Duration;
import java.util.Set;

@ApplicationScoped
public class JwtUtil {

    public String generateToken(Long userId) {
        return Jwt.issuer("expense-auth-service")
                .upn(String.valueOf(userId))
                .groups(Set.of("USER"))
                .expiresIn(Duration.ofHours(2))
                .sign();
    }
}
