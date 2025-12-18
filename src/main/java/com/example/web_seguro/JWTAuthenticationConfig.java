package com.example.web_seguro;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;

import static com.example.web_seguro.config.Constants.*;

@Configuration
public class JWTAuthenticationConfig {
     public String getJWTToken(UserDetails userDetails) {
        // Claims (datos que irán dentro del JWT)
        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities",
            userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList());
        claims.put("iss", ISSUER_INFO);

        // Generar el token
        String token = Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_TIME))
                .signWith(getSigningKey(SUPER_SECRET_KEY)) // usa la key desde Constants
                .compact();

        return TOKEN_BEARER_PREFIX + token;
    }

    private Key getSigningKey(String secret) {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
