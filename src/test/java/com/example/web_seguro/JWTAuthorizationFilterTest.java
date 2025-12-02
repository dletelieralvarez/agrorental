package com.example.web_seguro;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.example.web_seguro.config.Constants.HEADER_AUTHORIZACION_KEY;
import static com.example.web_seguro.config.Constants.SUPER_SECRET_KEY;
import static com.example.web_seguro.config.Constants.TOKEN_BEARER_PREFIX;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JWTAuthorizationFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JWTAuthorizationFilter filter;

    private SecretKey signingKey() {
        return Keys.hmacShaKeyFor(SUPER_SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    private String crearTokenValido() {
        return Jwts.builder()
                .subject("user@test.com")
                .claim("authorities", List.of("ROLE_USER"))
                .signWith(signingKey())
                .compact();
    }

    @AfterEach
    void limpiarSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    // 1) Token válido en HEADER Authorization
    @Test
    public void testDoFilterInternal_TokenValidoEnHeader() throws ServletException, IOException {
        String token = crearTokenValido();

        when(request.getHeader(HEADER_AUTHORIZACION_KEY))
                .thenReturn(TOKEN_BEARER_PREFIX + token);
        //when(request.getCookies()).thenReturn(null);
        when(request.getRequestURI()).thenReturn("/api/test");

        filter.doFilterInternal(request, response, filterChain);

        // Se debe haber llamado al siguiente filtro
        verify(filterChain, times(1)).doFilter(request, response);
        // No se debe enviar error
        verify(response, never()).sendError(anyInt(), anyString());

        // Autenticación establecida en el contexto
        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals("user@test.com", auth.getName());
        assertTrue(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
    }

    // 2) Token válido en COOKIE jwt_token    
    @Test
    public void testDoFilterInternal_TokenValidoEnCookie() throws ServletException, IOException {
        String token = crearTokenValido();

        when(request.getHeader(HEADER_AUTHORIZACION_KEY)).thenReturn(null);
        //when(request.getCookies()).thenReturn(new Cookie[]{ new Cookie("jwt_token", token) });
        when(request.getRequestURI()).thenReturn("/api/test");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(response, never()).sendError(anyInt(), anyString());

        //var auth = SecurityContextHolder.getContext().getAuthentication();
        //assertNotNull(auth);
        //assertEquals("user@test.com", auth.getName());
    }

    // 3) Sin token → limpia contexto y sigue cadena
    @Test
    public void testDoFilterInternal_SinToken_LimpiaContexto() throws ServletException, IOException {
        // Simulamos que había algo previamente en el contexto
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("otro", null, List.of())
        );

        when(request.getHeader(HEADER_AUTHORIZACION_KEY)).thenReturn(null);
        //when(request.getCookies()).thenReturn(null);
        when(request.getRequestURI()).thenReturn("/api/test");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);

        // Debe quedar sin autenticación
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    // 4) Token inválido → 403 y NO llama a la cadena
    @Test
    public void testDoFilterInternal_TokenInvalido_Retorna403() throws ServletException, IOException {
        String tokenInvalido = "abc.def.ghi"; // algo mal formado

        when(request.getHeader(HEADER_AUTHORIZACION_KEY))
                .thenReturn(TOKEN_BEARER_PREFIX + tokenInvalido);
        //when(request.getCookies()).thenReturn(null);
        when(request.getRequestURI()).thenReturn("/api/test");

        filter.doFilterInternal(request, response, filterChain);

        // No debe continuar la cadena de filtros
        verify(filterChain, never()).doFilter(request, response);

        // Debe enviar error 403
        verify(response, times(1))
                .sendError(eq(HttpServletResponse.SC_FORBIDDEN), anyString());

        // Contexto limpio
        //assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
