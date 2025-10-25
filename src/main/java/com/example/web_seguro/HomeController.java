package com.example.web_seguro;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam; 

@Controller
public class HomeController {

    @GetMapping({"/", "/index"})
    public String index(@RequestParam(name="name", required = false, defaultValue = "Seguridad y Calidad en el Desarrollo") String name,
                        Model model) {
        model.addAttribute("name", name);
        return "index";
    }
    
    @GetMapping("/account")
    public String getAccount(@RequestParam(name="name", required = false, defaultValue = "Seguridad y Calidad en el Desarrollo") String name,
                        Model model) {
        model.addAttribute("name", name);
        return "account";
    }


    // @GetMapping("/")
    // public String root(@RequestParam(name="name", required = false, defaultValue = "Seguridad y Calidad en el Desarrollo") String name, 
    // Model model) {
    //     model.addAttribute("name", name);
    //     return "index";
    // }
}
