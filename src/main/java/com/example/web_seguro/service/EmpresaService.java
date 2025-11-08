package com.example.web_seguro.service;

import org.springframework.stereotype.Service;

import com.example.web_seguro.model.Empresa;
import com.example.web_seguro.repository.EmpresaRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor // Lombok: genera un constructor con todos los atributos como parámetros
public class EmpresaService {
    
    private final  EmpresaRepository empresaRepository;

    /*public EmpresaService(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository;
    }*/

    public Empresa getByUuid(String uuid) {
        return empresaRepository.findByUuid(uuid)
            .orElseThrow(() -> new RuntimeException("Empresa no encontrada con UUID: " + uuid));
    }





}
