package com.example.web_seguro;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.web_seguro.service.CustomUserDetailsService;

import org.springframework.security.core.userdetails.UserDetails;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);


    @Autowired
    private JWTAuthenticationConfig jwtAuthtenticationConfig;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/account")
    public String showLoginPage() {
        return "account";
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
            logger.info("Intento de login para usuario: {}", username);
            
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            logger.info("Usuario encontrado: {}", userDetails.getUsername());
            logger.info("Password encriptada en BD: {}", userDetails.getPassword());
            logger.info("Password enviada (plain): {}", encryptedPass);

            boolean passwordValida = passwordEncoder.matches(encryptedPass, userDetails.getPassword());
            logger.info("¿Password válida?: {}", passwordValida);

            if (!passwordValida) {
                logger.warn("Contraseña incorrecta para usuario: {}", username);
                model.addAttribute("error", "Credenciales incorrectas");
                return "account";
            }

            logger.info("Login exitoso para: {}", username);
            String token = jwtAuthtenticationConfig.getJWTToken(userDetails);
            logger.info("Token generado: {}", token.substring(0, 20) + "...");

            // Guarda el token en cookie
            String tokenSinBearer = token.replace("Bearer ", "");
            Cookie jwtCookie = new Cookie("jwt_token", tokenSinBearer);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(24 * 60 * 60);
            response.addCookie(jwtCookie);

            logger.info("Cookie establecida, redirigiendo a /");
            return "redirect:/";
            
        } catch (Exception e) {
            logger.error("Error en login: ", e);
            model.addAttribute("error", "Error en autenticación: " + e.getMessage());
            return "account";
        }
    }
}


/* original 
public class LoginController {
    @GetMapping("/login")
    public String redirectToAccount() {
        return "redirect:/account";
    }

    // Página de login personalizada
    @GetMapping("/account")
    public String showLoginPage() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Si ya está autenticado, redirige al home
        if (auth != null && auth.isAuthenticated() && 
            !"anonymousUser".equals(auth.getPrincipal())) {
            return "redirect:/";
        }

        // Si no está autenticado, muestra la vista del formulario
        return "account";
    }

}

*/