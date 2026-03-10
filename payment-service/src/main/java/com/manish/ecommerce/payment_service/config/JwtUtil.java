package com.manish.ecommerce.payment_service.config;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    private final String SECRET_KEY = "secret123"; // must match UserService key

    // Extract username (subject)
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // Validate token structure and signature
    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // Internal method to extract claims
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                   .setSigningKey(SECRET_KEY)
                   .parseClaimsJws(token)
                   .getBody();
    }
}