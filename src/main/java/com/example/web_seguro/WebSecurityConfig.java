package com.example.web_seguro;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.context.annotation.Configuration;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

        @Autowired
        private JWTAuthorizationFilter jwtAuthorizationFilter;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http

                                .authorizeHttpRequests(authz -> authz
                                                .requestMatchers("/", "/home", "/account", "/login","/signup", "/css/**",
                                                                "/js/**", "/images/**", "/plugins/**","/error","/logout")
                                                .permitAll()
                                                .anyRequest().authenticated())
                                .exceptionHandling(ex -> ex
                                                .accessDeniedPage("/error") // rutas no autorizadas van aquí
                                )
                                .csrf(csrf -> csrf.disable())
                                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

}
