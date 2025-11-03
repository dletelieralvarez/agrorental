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
    
   /*  
    @GetMapping("/account")
    public String getAccount(@RequestParam(name="name", required = false, defaultValue = "Seguridad y Calidad en el Desarrollo") String name,
                        Model model) {
        model.addAttribute("name", name);
        return "account";
    }
 
   
    @GetMapping("/mis_empresasxxx")
    public String getMisEmpresas(@RequestParam(name="name", required = false, defaultValue = "Seguridad y Calidad en el Desarrollo") String name,
                        Model model) {
        model.addAttribute("name", name);
        return "mis_empresas";
    }

    @GetMapping("/mis_maquinarias")
    public String getMisMaquinarias(@RequestParam(name="name", required = false, defaultValue = "Seguridad y Calidad en el Desarrollo") String name,
                        Model model) {
        model.addAttribute("name", name);
        return "mis_maquinarias";
    }

    @GetMapping("/perfil")
    public String getPerfil(@RequestParam(name="name", required = false, defaultValue = "Seguridad y Calidad en el Desarrollo") String name,
                        Model model) {
        model.addAttribute("name", name);
        return "perfil";
    }
*/
    // @GetMapping("/")
    // public String root(@RequestParam(name="name", required = false, defaultValue = "Seguridad y Calidad en el Desarrollo") String name, 
    // Model model) {
    //     model.addAttribute("name", name);
    //     return "index";
    // }
}
