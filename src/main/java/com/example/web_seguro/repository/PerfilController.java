package com.example.web_seguro.repository;

import org.springframework.web.bind.annotation.GetMapping;

public class PerfilController {    

    @GetMapping("/perfil")
    public String perfil(){
        return "perfil";
    }

}
