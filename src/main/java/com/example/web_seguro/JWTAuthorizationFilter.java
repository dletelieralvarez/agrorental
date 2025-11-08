package com.example.web_seguro;

import java.util.stream.Collectors;

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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.example.web_seguro.config.Constants;
import java.util.Map;

import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;


import jakarta.servlet.http.Cookie;

import static com.example.web_seguro.config.Constants.*;

// https://medium.com/@haflan395/complete-guide-to-implementing-jwt-authentication-with-cookies-and-local-storage-in-react-and-dc9225fe259b
// https://josealopez.dev/en/blog/authentication-with-spring-security-and-jwt
// https://programacionymas.com/blog/jwt-vs-cookies-y-sesiones


@Component
public class JWTAuthorizationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JWTAuthorizationFilter.class);

    // 1 Extraigo el token del header Authorization o de la cookie
    private String getTokenFromRequest(HttpServletRequest request) {
        
        // Primero intento obtenerlo del header "Authorization: Bearer <token>"
        // Opción 1: Del header Authorization
        String authHeader = request.getHeader(HEADER_AUTHORIZACION_KEY);
        if (authHeader != null && authHeader.startsWith(TOKEN_BEARER_PREFIX)) {
            // Si existe, elimino el prefijo "Bearer " y retorno solo el token
            return authHeader.replace(TOKEN_BEARER_PREFIX, "").trim();
        }
        
        // Si no está en el header, busco en las cookies del request
        // Opción 2: De la cookie
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt_token".equals(cookie.getName())) {
                    logger.info("Token obtenido de cookie");
                    // Encontré la cookie jwt_token, retorno su valor
                    return cookie.getValue();
                }
            }
        }
        
        // Si no encontré el token en header ni en cookies, retorno null
        return null;
    }

    // 2 Valido la firma del token y extraigo los datos (claims)
    private Claims getClaimsFromToken(String token) {
        // Uso la librería JJWT para parsear el token
        return Jwts.parser()
                // Verifico que la firma sea válida usando mi clave secreta
                .verifyWith((SecretKey) getSigningKey(SUPER_SECRET_KEY))
                .build()
                // Parseo el token firmado
                .parseSignedClaims(token)
                // Extraigo los datos del token (usuario, roles, etc)
                .getPayload();
    }


    // 3 Creo la autenticación y la guardo en el contexto de seguridad
    private void setAuthentication(Claims claims) {
        // Obtengo la lista de autoridades (roles) del token
        List<String> authorities = (List<String>) claims.get("authorities");
        
        // Creo un token de autenticación con los datos del JWT
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                        claims.getSubject(),  // El usuario (email)
                        null,                 // No uso contraseña (ya está autenticado)
                        // Convierto cada rol a una autoridad de Spring
                        authorities.stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList())
                );
        
        // Guardo la autenticación en el contexto de seguridad de Spring
        // Así los controladores saben que el usuario está autenticado
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    // 4 Verifico si existe un token válido en el request
    private boolean isJWTValid(HttpServletRequest request) {
        // Retorno true si logré extraer un token, false si no existe
        return getTokenFromRequest(request) != null;
    }

    // 5 Obtengo la clave secreta para firmar/verificar tokens
    private Key getSigningKey(String secret) {
        // Delego a Constants para obtener la clave
        return Constants.getSigningKey(secret);
    }

    // 6 Método principal del filtro que se ejecuta en CADA request
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
                        
            // Log: muestro qué ruta está siendo accedida
            logger.info("=== Filtro JWT ===");
            logger.info("URI: {}", request.getRequestURI());
            
            // Verifico si el request tiene un token válido
            if (isJWTValid(request)) {
                // Obtengo el token
                String token = getTokenFromRequest(request);
                logger.info("Token encontrado");
                
                // Extraigo y valido los claims del token
                Claims claims = getClaimsFromToken(token);
                
                // Verifico que tenga autoridades (roles)
                if (claims.get("authorities") != null) {
                    // Establezco la autenticación en Spring Security
                    setAuthentication(claims);
                    logger.info("Autenticación establecida para: {}", claims.getSubject());
                    logger.info("Roles: {}", claims.get("authorities"));
                    //logger.info("Token: {}", token);
                } else {
                    // Si no tiene roles, limpio el contexto
                    logger.warn("Token sin authorities");
                    SecurityContextHolder.clearContext();
                }
            } else {
                // Si no hay token válido, limpio el contexto
                logger.info("JWT no válido o no encontrado");
                SecurityContextHolder.clearContext();
            }

            // Dejo que el request continúe su curso hacia el controlador
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException e) {
            // Si el token es inválido (expirado, malformado, etc)
            logger.error("JWT error: {}", e.getMessage());
            response.setContentType("text/html");
            // Envio un error 403 (Forbidden) al cliente
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
            return;
        }
    }
}

