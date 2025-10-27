package com.example.web_seguro;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Description;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebSecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests((requests) -> requests
                .requestMatchers( 
                    "/", "/home", "/index", "/account", "/error",
                    "/css/**", "/js/**", "/images/**", "/plugins/**", "/webjars/**").permitAll()
                .anyRequest()
                .authenticated()
            )
            .formLogin((form) -> form
                .loginPage("/account") // GET del login formualario
                .loginProcessingUrl("/login") //POST del login formulario
                .defaultSuccessUrl("/",true)
                .failureUrl("/account?error")
                .permitAll()
            )
            .logout((logout) -> logout.permitAll());

            return http.build();
    }

    @Bean
    @Description("In memory Userdetails service registered since DB doesn´t have user table")
    public UserDetailsService users() {
        UserDetails user = User.builder()
            .username("user")
            .password(passwordEncoder().encode("password"))
            .roles("USER")
            .build();
        UserDetails admin = User.builder()
            .username("admin")
            .password(passwordEncoder().encode("password"))
            .roles("USER","ADMIN")
            .build();
        return new InMemoryUserDetailsManager(user, admin);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
