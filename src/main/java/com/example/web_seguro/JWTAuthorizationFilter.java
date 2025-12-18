package com.example.web_seguro;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.example.web_seguro.config.Constants;

import java.io.IOException;
import java.security.Key;
import java.util.List;

import static com.example.web_seguro.config.Constants.*;

@Component
public class JWTAuthorizationFilter extends OncePerRequestFilter {

    private static final Logger LOG =
            LoggerFactory.getLogger(JWTAuthorizationFilter.class);

    // Evita duplicar el literal "authorities" (Sonar java:S1192)
    private static final String CLAIM_AUTHORITIES = "authorities";

    // 1) Extraigo el token del header Authorization o de la cookie
    private String getTokenFromRequest(HttpServletRequest request) {

        String authHeader = request.getHeader(HEADER_AUTHORIZACION_KEY);
        if (authHeader != null && authHeader.startsWith(TOKEN_BEARER_PREFIX)) {
            return authHeader.replace(TOKEN_BEARER_PREFIX, "").trim();
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt_token".equals(cookie.getName())) {
                    LOG.info("Token obtenido de cookie");
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    // 2) Valido la firma del token y extraigo los claims
    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) getSigningKey(SUPER_SECRET_KEY))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // 3) Creo la autenticación y la guardo en el contexto de seguridad
    @SuppressWarnings("unchecked")
    private void setAuthentication(Claims claims) {

        List<String> authorities =
                (List<String>) claims.get(CLAIM_AUTHORITIES);

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                        claims.getSubject(),
                        null,
                        authorities.stream()
                                .map(SimpleGrantedAuthority::new)
                                .toList() // java:S6204 → Stream.toList()
                );

        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    // 4) Verifico si existe un token en el request
    private boolean isJWTValid(HttpServletRequest request) {
        return getTokenFromRequest(request) != null;
    }

    // 5) Obtengo la clave secreta para firmar/verificar tokens
    private Key getSigningKey(String secret) {
        return Constants.getSigningKey(secret);
    }

    // 6) Filtro principal (se ejecuta en cada request)
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            LOG.info("=== Filtro JWT ===");
            LOG.info("URI: {}", request.getRequestURI());

            if (isJWTValid(request)) {
                String token = getTokenFromRequest(request);
                LOG.info("Token encontrado");

                Claims claims = getClaimsFromToken(token);

                if (claims.get(CLAIM_AUTHORITIES) != null) {
                    setAuthentication(claims);
                    LOG.info("Autenticación establecida para: {}", claims.getSubject());
                    LOG.info("Roles: {}", claims.get(CLAIM_AUTHORITIES));
                } else {
                    LOG.warn("Token sin authorities");
                    SecurityContextHolder.clearContext();
                }
            } else {
                LOG.info("JWT no válido o no encontrado");
                SecurityContextHolder.clearContext();
            }

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException e) {
            LOG.error("JWT error: {}", e.getMessage());
            response.setContentType("text/html");
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        }
    }
}
