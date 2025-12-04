package com.example.web_seguro;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.junit.jupiter.api.Assertions.*;

class WebSecurityConfigBeanTest {

    // No se necesita inyectar nada, estos metodos no usan jwtAuthorizationFilter
    private final WebSecurityConfig config = new WebSecurityConfig();

    @Test
    void passwordEncoder_debeSerBCryptYFuncionar() {
        PasswordEncoder encoder = config.passwordEncoder();

        assertNotNull(encoder);
        assertTrue(encoder instanceof BCryptPasswordEncoder);

        String raw = "secreto123";
        String encoded = encoder.encode(raw);
        assertTrue(encoder.matches(raw, encoded));
    }

    @Test
    void corsConfigurationSource_debeConfigurarOrigenesYMetodosCorrectos() {
        CorsConfigurationSource source = config.corsConfigurationSource();
        assertNotNull(source);

        MockHttpServletRequest request =
                new MockHttpServletRequest("GET", "/api/test/recurso");

        CorsConfiguration corsConfig = source.getCorsConfiguration(request);
        assertNotNull(corsConfig);

        assertEquals(
                java.util.List.of("https://www.tusitio.com"),
                corsConfig.getAllowedOrigins()
        );
        assertEquals(
                java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"),
                corsConfig.getAllowedMethods()
        );
        assertEquals(
                java.util.List.of("Authorization", "Content-Type", "X-Requested-With"),
                corsConfig.getAllowedHeaders()
        );
        assertEquals(
                java.util.List.of("Authorization"),
                corsConfig.getExposedHeaders()
        );
        assertFalse(corsConfig.getAllowCredentials());
        assertEquals(3600L, corsConfig.getMaxAge());
    }
}
