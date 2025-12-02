package com.example.web_seguro.controller;

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

/*
 * Controlador que se encarga de la actualización del persil
 * de cada usario del sistema
 * los usuarios no pueden cambiar su rol, que en la mayoria sin USER 
 */
@Controller
public class PerfilController {

    private static final Logger logger = LoggerFactory.getLogger(PerfilController.class);

    /*
     * Este bloque es una anotación que marca un método que se eejcutará
     * automáticamente una vez que el nbean se haya creado y todas sus
     * dependencias hayan sido creadas.
     */
    @PostConstruct
    public void init() {
        logger.warn("⚡ Logger del PerfilController inicializado correctamente");
    }

    // Inyección automática de dependiencia
    @Autowired
    private UsuarioRepository usuarioRepository;

    /*
     * Obtiene la rura de perfil que muestra la vista perfil al usuario
     * Esta vista tiene un formulario de acción POST para actualiza
     * campos básicos del usuario.
     */
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

        //model.addAttribute("email", username);
        model.addAttribute("rol", roles);

        return "perfil";
    }
}