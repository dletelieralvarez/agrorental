package com.example.web_seguro;

 import org.springframework.stereotype.Controller;
   import org.springframework.web.bind.annotation.GetMapping;
   import org.springframework.web.bind.annotation.PostMapping;
   import jakarta.servlet.http.Cookie;
   import jakarta.servlet.http.HttpServletResponse;
   import org.slf4j.Logger;
   import org.slf4j.LoggerFactory;
   import org.springframework.security.core.context.SecurityContextHolder;
@Controller
public class LogoutController {
    private static final Logger logger = LoggerFactory.getLogger(LogoutController.class);

    @GetMapping("/salir")
    public String logoutGet() {
        logger.info("GET /logout");
        return "redirect:/login";
    }

    @PostMapping("/salir")
    public String logout(HttpServletResponse response) {
        logger.info("Logout solicitado");
        
        // Elimina la cookie jwt_token
        Cookie jwtCookie = new Cookie("jwt_token", null);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0);  // Esto elimina la cookie
        response.addCookie(jwtCookie);
        
        // Limpia el contexto de seguridad
        SecurityContextHolder.clearContext();
        
        logger.info("Sesión cerrada");
        
        // Redirige al login
        return "redirect:/login";
    }
}
