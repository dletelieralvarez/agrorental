package com.example.web_seguro;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;

import com.example.web_seguro.model.Usuario; 
import com.example.web_seguro.service.UsuarioService;

@Controller
public class RegistroController {
   
    @Autowired
    private final UsuarioService usuarioService;

    public RegistroController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/signup")
    public String mostrarFormularioRegistro(Model model) {     
                      
        model.addAttribute("usuario",new Usuario());
        return "signup";
    }    
 
    @PostMapping("/signup")
    public String registrarUsuario(@Valid @ModelAttribute("usuario") Usuario usuario, BindingResult result, Model model) {
        if(result.hasErrors()) {
            result.getFieldErrors().forEach(e -> 
            System.out.println("Field error: " + e.getField() + " -> " + e.getDefaultMessage()));
            return "signup";
        }

        try{
            usuarioService.registrarUsuario(usuario);
            return "redirect:/login?registroExitoso";
        }
        catch(IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "signup";
        }
    }

}
