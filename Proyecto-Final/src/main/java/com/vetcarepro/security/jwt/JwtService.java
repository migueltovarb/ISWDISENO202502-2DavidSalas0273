package com.vetcarepro.security.jwt;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

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

    public String generateToken(UserDetails userDetails, Map<String, Object> claims) {
        Instant now = Instant.now();
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(userDetails.getUsername())
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now.plusSeconds(expirationMinutes * 60)))
            .signWith(Keys.hmacShaKeyFor(secret), SignatureAlgorithm.HS256)
            .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(Keys.hmacShaKeyFor(secret))
            .build()
            .parseClaimsJws(token)
            .getBody();
    }
}
