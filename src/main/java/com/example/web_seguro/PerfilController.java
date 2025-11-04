package com.example.web_seguro;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.web_seguro.model.Usuario;
import com.example.web_seguro.repository.UsuarioRepository;

import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Controller
public class PerfilController {

    private static final Logger logger = LoggerFactory.getLogger(PerfilController.class);

    @PostConstruct
public void init() {
    logger.warn("⚡ Logger del PerfilController inicializado correctamente");
}
@Autowired
private UsuarioRepository usuarioRepository;

    @GetMapping("/perfil")
    public String getPerfil(@AuthenticationPrincipal Object principal, Model model) {

      
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String username = auth.getName();
        String roles = auth.getAuthorities().toString();

        logger.info("Usuario autenticado: {}", username);
        logger.info("Roles: {}", roles);


        String email = username;
        
    Usuario usuario = usuarioRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    model.addAttribute("email", usuario.getEmail());
    model.addAttribute("nombres", usuario.getNombres());
    model.addAttribute("paterno", usuario.getPrimerApellido());
    model.addAttribute("materno", usuario.getSegundoApellido());

        model.addAttribute("email", username);
        model.addAttribute("rol", roles);




        return "perfil";
    }
}
/* public class PerfilController {
    private static final Logger logger = LoggerFactory.getLogger(PerfilController.class);

    @GetMapping("/perfil")
   public String getPerfil(
           @RequestParam(name = "name", required = false, defaultValue = "Seguridad y Calidad en el Desarrollo") String name,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {

        model.addAttribute("name", name);

        logger.warn("=== Perfil Controller ===");
           
        // Los datos del usuario autenticado
        if (userDetails != null) {

            logger.info("Usuario autenticado: {}", userDetails.getUsername());
            logger.info("Roles: {}", userDetails.getAuthorities());
            
            model.addAttribute("email", userDetails.getUsername());
            model.addAttribute("rol", userDetails.getAuthorities().toString());
        } else {
            logger.warn("No hay usuario autenticado");

        }
        
        return "perfil";
    }

}
*/