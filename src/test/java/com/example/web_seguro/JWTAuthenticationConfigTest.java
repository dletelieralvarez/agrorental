package com.example.web_seguro;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.crypto.SecretKey;

import java.util.Date;

import static com.example.web_seguro.config.Constants.*;
import static org.junit.jupiter.api.Assertions.*;

public class JWTAuthenticationConfigTest {
    
private final JWTAuthenticationConfig jwtConfig = new JWTAuthenticationConfig();

    //helper para obtener la misma key 
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SUPER_SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    public void testGetJWTToken_GeneraTokenValidoConClaims() {
        // ARRANGE
        UserDetails userDetails = new User(
                "user@test.com",
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        // ACT
        String tokenConPrefijo = jwtConfig.getJWTToken(userDetails);

        // ASSERT básico: no nulo y empieza con el prefijo
        assertNotNull(tokenConPrefijo);
        assertTrue(tokenConPrefijo.startsWith(TOKEN_BEARER_PREFIX));

        // Quita el prefijo "Bearer "
        String token = tokenConPrefijo.substring(TOKEN_BEARER_PREFIX.length());

        // Parsea el token usando la misma key
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        // Verifica subject username
        assertEquals("user@test.com", claims.getSubject());

        // Verifica issuer
        assertEquals(ISSUER_INFO, claims.get("iss"));

        // Verifica authorities
        @SuppressWarnings("unchecked")
        List<String> authorities = (List<String>) claims.get("authorities");
        assertNotNull(authorities);
        assertTrue(authorities.contains("ROLE_USER"));
    }

    @Test
    public void testGetJWTToken_TieneFechaExpiracionFutura() {
        // ARRANGE
        UserDetails userDetails = new User(
                "user@test.com",
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        // ACT
        String tokenConPrefijo = jwtConfig.getJWTToken(userDetails);
        String token = tokenConPrefijo.substring(TOKEN_BEARER_PREFIX.length());

        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Date ahora = new Date();
        Date expiracion = claims.getExpiration();

        // ASSERT
        assertNotNull(expiracion);
        assertTrue(expiracion.after(ahora),
                "La fecha de expiración del token debe ser posterior a 'ahora'");
    }
}
