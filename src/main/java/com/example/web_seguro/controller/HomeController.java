package com.example.web_seguro.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam; 

@Controller
public class HomeController {

    // punto de entrada de la aplicación de arriendo de maquinarias
    @GetMapping({"/", "/index"})
    public String index(@RequestParam(name="name", required = false, defaultValue = "Seguridad y Calidad en el Desarrollo") String name,
                        Model model) {
        model.addAttribute("name", name);
        return "index";
    }

}
