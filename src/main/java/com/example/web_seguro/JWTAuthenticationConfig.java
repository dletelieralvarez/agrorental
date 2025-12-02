package com.example.web_seguro;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        // Asignar roles por defecto 
        List<GrantedAuthority> grantedAuthorities =
                userDetails.getAuthorities().stream().collect(Collectors.toList());


        // Claims (datos que irán dentro del JWT)
        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities", grantedAuthorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
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


/* 

@Configuration
public class JWTAuthenticationConfig {
     public String getJWTToken(String username) {
        // Asignar roles por defecto 
        List<GrantedAuthority> grantedAuthorities =
                AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER");

        // Claims (datos que irán dentro del JWT)
        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities", grantedAuthorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        claims.put("iss", ISSUER_INFO);

        // Generar el token
        String token = Jwts.builder()
                .claims(claims)
                .subject(username)
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
*/