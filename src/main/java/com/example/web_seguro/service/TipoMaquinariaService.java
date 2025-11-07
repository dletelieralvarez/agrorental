package com.example.web_seguro.service;

import com.example.web_seguro.repository.TipoMaquinariaRepository;
import org.springframework.stereotype.Service;
import com.example.web_seguro.model.TipoMaquinaria;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID; 

@Service
public class TipoMaquinariaService {
    private final TipoMaquinariaRepository tipoMaquinariaRepository; 
    
    public TipoMaquinariaService(TipoMaquinariaRepository tipoMaquinariaRepository) {
        this.tipoMaquinariaRepository = tipoMaquinariaRepository;        
    }

    @Transactional
    public TipoMaquinaria guardarTipoMaquinaria(TipoMaquinaria tipo)
    {
        if(tipo.getUuid() == null || tipo.getUuid().isBlank())
        {
            tipo.setUuid(UUID.randomUUID().toString());
        }

        return tipoMaquinariaRepository.save(tipo); 
    }

    @Transactional
    public void eliminarTipoMaquinaria(Long id){
        if(!tipoMaquinariaRepository.existsById(id)){
            throw new RuntimeException("El tipo de maquinaria no existe"); 
        }
        tipoMaquinariaRepository.deleteById(id);
    }

    @Transactional
    public TipoMaquinaria actualizaTipoMaquinaria(Long id, TipoMaquinaria tipoMaq){
        TipoMaquinaria tipo = tipoMaquinariaRepository.findById(id)
            .orElseThrow(()-> new RuntimeException("Tipo de maquinaria no encontrada")); 

        tipo.setDescripcion(tipoMaq.getDescripcion());

        return tipoMaquinariaRepository.save(tipo); 
    }

    //listar todas los tipos 
    public List<TipoMaquinaria> listaTipoMaquinarias(){
        return tipoMaquinariaRepository.findAll(); 
    }

    //listar tipo por id
    public Optional<TipoMaquinaria> buscarTipoMaquinariaPorID(Long id){
        return tipoMaquinariaRepository.findById(id); 
    }

    //busca por uuid
    public Optional<TipoMaquinaria> buscarTipoMaquinariaPorUuid(String uuid){
        return tipoMaquinariaRepository.findByuuid(uuid); 
    }
}
