package com.example.web_seguro;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cliente")
public class ClienteController {
    
    @GetMapping("/perfil")
    public String perfil() {
        return "perfil";
    }

    @GetMapping("/mis_empresas")
    public String mis_empresas() {
        return "mis_empresas";
    }

    @GetMapping("/mis_maquinarias")
    public String pagina3() {
        return "mis_maquinarias";
    }
}

