package com.lms.backend.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    private final SecretKey signingKey;
    private final long accessTokenExpiryMinutes;

    public JwtUtil(
            @Value("${backend.jwt.secret:defaultSecretKeyFallbackMustBeAtLeast32CharactersLong!!!}") String secret,
            @Value("${backend.jwt.access-token-expiry-minutes:60}") long accessTokenExpiryMinutes
    ) {
        // Fallback protection if the string length is too short
        if (secret == null || secret.getBytes().length < 32) {
            secret = "defaultSecretKeyFallbackMustBeAtLeast32CharactersLong!!!";
        }
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessTokenExpiryMinutes = accessTokenExpiryMinutes;
    }


    public String generateToken(Long userId, String email, String role, Long teacherId){
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenExpiryMinutes *60 *1000);

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", role);
        if(teacherId != null){
            claims.put("TeacherId", teacherId);
        }
        return Jwts.builder()

                .setSubject(email)
                .addClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isValid(String token){
        try{
            parseClaims(token);
            return true;
        }catch(Exception e){
            return false;
        }
    }
    public String extraEmail(String token){
        return parseClaims(token).getSubject();
    }
}
