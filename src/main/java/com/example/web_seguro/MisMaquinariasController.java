package com.example.web_seguro;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.ui.Model;

@Controller
public class MisMaquinariasController {
    @GetMapping("/mis_maquinarias")
    
    public String getMisMaquinarias(@RequestParam(name="name", required = false, defaultValue = "Seguridad y Calidad en el Desarrollo") String name,
                        Model model) {
        model.addAttribute("name", name);
        return "mis_maquinarias";
    }
}

