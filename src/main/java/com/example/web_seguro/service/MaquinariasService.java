package com.example.web_seguro.service;

import org.springframework.stereotype.Service;

import com.example.web_seguro.model.Maquinarias;
import com.example.web_seguro.model.TipoMaquinaria;
import com.example.web_seguro.repository.MaquinariasRepository;
import com.example.web_seguro.repository.TipoMaquinariaRepository;

import jakarta.transaction.Transactional;

import java.beans.Transient;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MaquinariasService {
    
    private final MaquinariasRepository maquinariasRepository; 
    private final TipoMaquinariaRepository tipoMaquinariaRepository; 
    //private final EmpresasRepository empresasRepository; 

    public MaquinariasService(MaquinariasRepository maquinariasRepository, 
        TipoMaquinariaRepository tipoMaquinariaRepository)//, 
        //EmpresasRepository EmpresasRepository)
    {
        this.maquinariasRepository = maquinariasRepository; 
        this.tipoMaquinariaRepository = tipoMaquinariaRepository; 
        //this.empresasRepository = empresasRepository; 
    }

    @Transactional
    public Maquinarias guardarMaquinaria(Maquinarias maq){
        if(maq.getUuid() == null || maq.getUuid().isBlank()){
            maq.setUuid(UUID.randomUUID().toString());
        }
        return maquinariasRepository.save(maq); 
    }

    //crea una maquinaria recibiendo id de empresa y tipo maquinaria
    @Transactional
    public Maquinarias guardarMaquinariaRel(Maquinarias maq, Long tipoMaquinariaId)//, Long empresaId)
    {
        //Empresas empresa = empresasRepository.findById(empresaId)
        //    .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada (id=" + empresaId + ")"));

        TipoMaquinaria tipo = tipoMaquinariaRepository.findById(tipoMaquinariaId)
            .orElseThrow(() -> new IllegalArgumentException("Tipo de Maquinaria no encontrada (id=" +tipoMaquinariaId + ")"));
    
        if(maq.getUuid() == null || maq.getUuid().isBlank()){
            maq.setUuid(UUID.randomUUID().toString());
        }

        //maq.setEmpresas(empresa);
        maq.setTiposMaquinarias(tipo); 
        return maquinariasRepository.save(maq); 

    }

    @Transactional
    public Maquinarias actualizarMaquinaria(Long id, Maquinarias maq, Long tipoMaquinariaId)//, Long empresaId)
    {
        Maquinarias existente = maquinariasRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Maquinaria no encontrada (id=" +id + ")"));
        
        existente.setDescripcion(maq.getDescripcion());
        existente.setAnioFabricacion(maq.getAnioFabricacion());
        existente.setCapacidad(maq.getCapacidad());
        existente.setMantencion(maq.getMantencion());
        existente.setCondicionArriendo(maq.getCondicionArriendo());
        existente.setMediosPago(maq.getMediosPago());
        existente.setFechaDisponible(maq.getFechaDisponible());
        existente.setPatente(maq.getPatente());
        existente.setDisponible(maq.getDisponible()); 

        //if(empresaId != null){
        //    Empresas empresa = empresasRepository.findById(empresaId)
        //        .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada (id=" +empresaId + ")"));
        //      existente.setEmpresas(empresa); 
        //}

        if(tipoMaquinariaId != null)
        {
            TipoMaquinaria tipo = tipoMaquinariaRepository.findById(tipoMaquinariaId)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de Maquinaria no encontrada (id=" +tipoMaquinariaId + ")"));
            
            existente.setTiposMaquinarias(tipo); 
        }

        return maquinariasRepository.save(existente); 
    }

    @Transactional
    public void eliminarMaquinaria(Long id){
        if(!maquinariasRepository.existsById(id)){
            throw new IllegalArgumentException("La maquinaria no existe (id=" + id + ")"); 
        }
        maquinariasRepository.deleteById(id);
    }

    public List<Maquinarias> listaMaquinarias(){
        return maquinariasRepository.findAll(); 
    }

    public Optional<Maquinarias> buscaMaquinariaPorId(Long id){
        return maquinariasRepository.findById(id); 
    }

    public Optional<Maquinarias> buscaMaquinariaPorUuid(String uuid){
        return maquinariasRepository.findByUuid(uuid); 
    }

    /*
    public List<Maquinarias> listarMaquinariaPorEmpresa(Long empresaId){
        Empresa empresa = empresasRepository.findById(empresaId)
            .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada (id=" +empresaId+ ")"));
        return maquinariasRepository.findByEmpresas(empresa); 
    }
    */

    public List<Maquinarias> listaMaquinariasPorTipo(Long tipoId){
        TipoMaquinaria tipo = tipoMaquinariaRepository.findById(tipoId)
            .orElseThrow(()-> new IllegalArgumentException("Tipo de maquinaria no encontrada (id=" +tipoId+ ")"));
        return maquinariasRepository.findByTiposMaquinarias(tipo);  
    }

    public List<Maquinarias> listaMaquinariasDisponibles(){
        return maquinariasRepository.findByDisponible("SI"); 
    }

}
