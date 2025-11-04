package com.example.web_seguro.service;

import org.springframework.stereotype.Service;

import com.example.web_seguro.model.TipoCultivo;
import com.example.web_seguro.repository.TipoCultivoRepository;

@Service
public class TipoCultivoService {

    private final TipoCultivoRepository tipoCultivoRepository;

    public TipoCultivoService(TipoCultivoRepository tipoCultivoRepository) {
        this.tipoCultivoRepository = tipoCultivoRepository;
    }

    public TipoCultivo getByUuid(String uuid) {
        return tipoCultivoRepository.findByUuid(uuid)
            .orElseThrow(() -> new RuntimeException("Tipo de cultivo no encontrado con UUID: " + uuid));
    }
}