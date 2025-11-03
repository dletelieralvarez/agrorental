package com.example.web_seguro;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RecetasController {
    @GetMapping("/recetas")
    public String getRecetas(@RequestParam(name="name", required = false, defaultValue = "Seguridad y Calidad en el Desarrollo") String name,
                        Model model) {
        model.addAttribute("name", name);
        return "recetas";
    }
}
