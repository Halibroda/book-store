package com.epam.rd.autocode.spring.project.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JwtService {
    private final Key key;
    private final long accessTtlSeconds;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.access-ttl-seconds:1800}") long accessTtlSeconds) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        this.accessTtlSeconds = accessTtlSeconds;
    }

    public String generateAccessToken(UserDetails user) {
        Instant now = Instant.now();
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        return Jwts.builder()
            .setSubject(user.getUsername())
            .addClaims(claims)
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now.plusSeconds(accessTtlSeconds)))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    public boolean isValid(String token) {
        try {
            parseAllClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getUsername(String token) {
        return parseAllClaims(token).getSubject();
    }

    public List<String> getRoles(String token) {
        Object r = parseAllClaims(token).get("roles");
        if (r instanceof Collection<?> c) {
            return c.stream().map(Object::toString).collect(Collectors.toList());
        }
        return List.of();
    }

    private Claims parseAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
            .parseClaimsJws(token).getBody();
    }
}
