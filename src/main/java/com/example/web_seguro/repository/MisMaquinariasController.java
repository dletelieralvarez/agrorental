package com.example.web_seguro.repository;

import org.springframework.web.bind.annotation.GetMapping;

public class MisMaquinariasController {
     @GetMapping("/mis_maquinarias")
    public String misMaquinarias(){
        return "mis_maquinarias";
    }
}
