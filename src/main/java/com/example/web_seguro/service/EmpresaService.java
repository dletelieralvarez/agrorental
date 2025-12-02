package com.example.web_seguro.service;

import java.util.List;

import org.springframework.data.domain.Sort;
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

    public List<Empresa> listaEmpresas() {
        return empresaRepository.findAll(Sort.by("razonSocial").ascending());
    }

}
