package org.acme.auth.security;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class JwtUtil {

    public String generateToken(Long userId) {
        return Jwt.issuer("expense-auth-service")
                .upn(String.valueOf(userId))
                .groups(Set.of("USER"))
                .expiresIn(Duration.ofHours(2))
                .sign();
    }

    public String generateAccessToken(Long userId) {
        return Jwt.issuer("expense-auth-service")
                .upn(String.valueOf(userId))
                .claim("type", "access")
                .expiresIn(Duration.ofMinutes(15))
                .sign();
    }

    public String generateRefreshToken(Long userId) {
        return Jwt.issuer("expense-auth-service")
                .upn(String.valueOf(userId))
                .claim("type", "refresh")
                .claim("jti", UUID.randomUUID().toString())
                .expiresIn(Duration.ofDays(7))
                .sign();
    }

}
