package org.acme.auth.utils;

import java.time.Duration;
import java.util.UUID;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class JwtUtil {

    public String generateAccessToken(Long userId) {
        return Jwt.issuer("expense-auth-service")
                .upn(String.valueOf(userId))
                .claim("type", "access")
                .expiresIn(Duration.ofDays(15))
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
