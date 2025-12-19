package org.acme.auth.security;

import java.time.Duration;
import java.util.Set;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class JwtUtil {

    // Generate access token valid for 2 hours
    public String generateToken(String email) {
        return Jwt.issuer("expense-auth-service")
                .upn(email)
                .groups(Set.of("USER"))
                .expiresIn(Duration.ofHours(2))
                .sign();
    }

    // Generate refresh token valid for 7 days
    public String generateRefreshToken(String email) {
        return Jwt.issuer("expense-auth-service")
                .upn(email)
                .claim("typ", "refresh")
                .expiresIn(Duration.ofDays(7))
                .sign();
    }

}
