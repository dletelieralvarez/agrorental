package com.example.web_seguro;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.RequestDispatcher;

import org.springframework.context.annotation.Configuration;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

        @Autowired
        private JWTAuthorizationFilter jwtAuthorizationFilter;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .authorizeHttpRequests(authz -> authz
                                .requestMatchers("/", "/home", "/account", "/login","/signup", "/css/**",
                                                                "/js/**", "/images/**", "/plugins/**","/error","/salir",
                                                                "/logout",
                                                                "/webjars/**","/favicon.ico")
                                .permitAll()
                                .anyRequest().authenticated()
                                )
                                .exceptionHandling(ex -> ex
                                        //  .accessDeniedPage("/error") // rutas no autorizadas van aquí
                                        .accessDeniedHandler((request, response, exception) -> {
                                                        request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, 403);
                                                        request.getRequestDispatcher("/error").forward(request, response);
                                                     })
                                )
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .csrf(csrf -> csrf.disable())
                                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                                .sessionManagement(session -> session
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                //aqui se agrega la csp
                                .headers(headers -> headers
                                .contentSecurityPolicy(csp -> csp
                                        .policyDirectives(
                                        "default-src 'self'; " +
                                        "script-src 'self' https://cdnjs.cloudflare.com https://ajax.googleapis.com; " +
                                        "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com; " +
                                        "img-src 'self' data:; " +
                                        "font-src 'self' https://fonts.gstatic.com; " +
                                        "frame-src 'self'; " +
                                        "frame-ancestors 'none';"
                                        )
                                )
                                
                                .frameOptions(fo -> fo.deny())   //navegadores antiguos evita clickjacking
                                //.contentTypeOptions() // agrega X-content-type-Options: nosniff
                                .contentTypeOptions(cto -> { }) // nueva forma de agregar X-content-type:nosniff
                                );

                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowedOrigins(List.of("https://www.tusitio.com"));         
                config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
                config.setAllowedHeaders(List.of("Authorization","Content-Type","X-Requested-With"));
                config.setExposedHeaders(List.of("Authorization")); 
                config.setAllowCredentials(false); // si se utiliza cookies/credenciales;NO puedes usar, debe ir en false'
                config.setMaxAge(3600L);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                // Limita el mapeo a tus endpoints de API:
                source.registerCorsConfiguration("/api/**", config);
                return source;
        }
}
