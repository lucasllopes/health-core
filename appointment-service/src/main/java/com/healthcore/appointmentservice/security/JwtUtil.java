package com.healthcore.appointmentservice.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.secret.refresh}")
    private String refreshSecret;

    private SecretKey jwtSecretKey;

    private SecretKey refreshKey;

    private final int jwtExpirationMs = 900_000;

    private final int refreshExpirationMs = 900_000;

    @PostConstruct
    public void init() {
        jwtSecretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        refreshKey = Keys.hmacShaKeyFor(refreshSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .issuer("HealthCore")
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(jwtSecretKey)
                .compact();
    }
    public String generateTokenWithRole(String username, String role) {
        return Jwts.builder()
                .issuer("HealthCore")
                .subject(username)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(jwtSecretKey)
                .compact();
    }

    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .issuer("HealthCore")
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshExpirationMs))
                .claim("type", "refresh")
                .signWith(refreshKey)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(jwtSecretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    public String getUsernameFromRefreshToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(refreshKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    public String getRoleFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(jwtSecretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("role", String.class);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(jwtSecretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException ex) {
            throw new RuntimeException("Token expirado. Realize novamente o login.");
        } catch (UnsupportedJwtException | MalformedJwtException ex) {
            log.error("Token invalido: {}", ex.getMessage());
            return false;
        } catch (Exception ex) {
            log.error("Error message: {}", ex.getMessage());
            return false;
        }

    }

    public String refreshAccessToken(String refreshToken, String username) {
        try {
        Claims claims = Jwts.parser()
                .verifyWith(refreshKey)
                .build()
                .parseSignedClaims(refreshToken)
                .getPayload();

        if (!"refresh".equals(claims.get("type"))) {
            throw new IllegalArgumentException("Token não é um refresh válido.");
        }

        return generateToken(username);
        } catch (ExpiredJwtException ex) {
            throw new RuntimeException("Refresh token expirado. Realize novamente o login.");
        }
    }
}
