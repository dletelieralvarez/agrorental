package com.example.web_seguro.repository;

import org.springframework.web.bind.annotation.GetMapping;

public class MisEmpresasController {
     @GetMapping("/mis_empresas")
    public String misEmpresas(){
        return "mis_empresas";
    }
}
