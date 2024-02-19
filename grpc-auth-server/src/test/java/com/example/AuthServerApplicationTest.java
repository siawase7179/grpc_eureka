package com.example;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.security.Key;
import java.util.Date;

import javax.crypto.spec.SecretKeySpec;

import org.junit.jupiter.api.Test;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;


public class AuthServerApplicationTest {
    @Test
    public void requestToken() {
        byte[] keyBytes = "ThisisgrpcTokenSecretKey!!!!!!!!".getBytes();
        Key secretKey = new SecretKeySpec(keyBytes, io.jsonwebtoken.SignatureAlgorithm.HS256.getJcaName());
        String id = "id";
        
        Date expiration = new Date(System.currentTimeMillis() + 3600000);

        String jwtToken = Jwts.builder()
                .claim("id", id)
                .setExpiration(expiration)
                .signWith(secretKey)
                .compact();

        assertDoesNotThrow(() -> {
            Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(jwtToken);
            Claims claims = claimsJws.getBody();
            String parsedId = claims.get("id", String.class);
            assertEquals(id, parsedId);
        });
    }

    @Test
    public void expirtedToken() {
        byte[] keyBytes = "ThisisgrpcTokenSecretKey!!!!!!!!".getBytes();
        Key secretKey = new SecretKeySpec(keyBytes, io.jsonwebtoken.SignatureAlgorithm.HS256.getJcaName());
        String id = "id";
        
        Date expiration = new Date(System.currentTimeMillis());

        String jwtToken = Jwts.builder()
                .claim("id", id)
                .setExpiration(expiration)
                .signWith(secretKey)
                .compact();

    
        assertThrows(ExpiredJwtException.class, () -> {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(jwtToken);
        });
    }
}
