package com.example.web_seguro;

import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.HttpHeaders;

import com.example.web_seguro.service.CustomUserDetailsService;

import org.springframework.security.core.userdetails.UserDetails;
import jakarta.servlet.http.HttpServletResponse;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    // sin @Autowired, inyectadas por constructor
    private final JWTAuthenticationConfig jwtAuthenticationConfig;
    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private static final String VIEW_ACCOUNT = "account";

    // Constructor para la inyección de dependencias
    public LoginController(JWTAuthenticationConfig jwtAuthenticationConfig,
                           CustomUserDetailsService userDetailsService,
                           PasswordEncoder passwordEncoder) {
        this.jwtAuthenticationConfig = jwtAuthenticationConfig;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/account")
    public String showLoginPage() {
        //return "account";
        return VIEW_ACCOUNT; 
    }

    @GetMapping("/login")
    public String redirectToAccount() {
        return "redirect:/account";
    }

    @PostMapping("/login")
    public String login(@RequestParam("user") String username,
                        @RequestParam("encryptedPass") String encryptedPass,
                        HttpServletResponse response,
                        Model model) {

        try {

            String sanitizedUsername = username.replaceAll("[\\n\\r\\t]", "_");
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            boolean passwordValida = passwordEncoder.matches(encryptedPass, userDetails.getPassword());

            if (!passwordValida) {
                logger.warn("Contraseña incorrecta para usuario: {}", sanitizedUsername);
                model.addAttribute("error", "Credenciales incorrectas");
                return "account";
            }

            String token = jwtAuthenticationConfig.getJWTToken(userDetails);
            String tokenSinBearer = token.replace("Bearer ", "");
            boolean isProd = "prod".equals(System.getenv("ENV"));
            ResponseCookie cookie = ResponseCookie.from("jwt_token", tokenSinBearer)
                    .httpOnly(true)
                    .secure(isProd)        // en producción normalmente true
                    .sameSite("Strict")   // o "Lax" 
                    .path("/")
                    .maxAge(24 * 60 * 60)
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            logger.info("Cookie establecida, redirigiendo a /");

            return "redirect:/";

        } catch (Exception e) {
            model.addAttribute("error", "Error en autenticación: " + e.getMessage());
            //return "account";
            return VIEW_ACCOUNT; 
        }
    }

    String sanitize(String value) {
        if (value == null) return "";
        return value.replaceAll("[\\n\\r\\t]", "_");
    }
}