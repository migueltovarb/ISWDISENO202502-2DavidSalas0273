package com.vetcarepro.security.jwt;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.vetcarepro.domain.entity.UserAccount;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtService {

    private final byte[] secret;
    private final long expirationMinutes;

    public JwtService(
        @Value("${jwt.secret}") String secret,
        @Value("${jwt.expiration-minutes:60}") long expirationMinutes
    ) {
        this.secret = Decoders.BASE64.decode(secret);
        this.expirationMinutes = expirationMinutes;
    }

    public String generateToken(UserAccount userAccount) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userAccount.getId());
        claims.put("email", userAccount.getEmail());
        claims.put("role", userAccount.getRole().name());
        if (userAccount.getReferenceId() != null) {
            claims.put("referenceId", userAccount.getReferenceId());
        }
        return buildToken(claims, userAccount.getEmail());
    }

    private String buildToken(Map<String, Object> claims, String subject) {
        Instant now = Instant.now();
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now.plusSeconds(expirationMinutes * 60)))
            .signWith(Keys.hmacShaKeyFor(secret), SignatureAlgorithm.HS256)
            .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String email = extractEmail(token);
        return email.equalsIgnoreCase(userDetails.getUsername())
            && userDetails.isEnabled()
            && !isTokenExpired(token);
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", String.class));
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(extractAllClaims(token));
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(Keys.hmacShaKeyFor(secret))
            .build()
            .parseClaimsJws(token)
            .getBody();
    }
}
