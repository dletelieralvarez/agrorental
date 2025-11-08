package com.example.web_seguro.service;

import org.springframework.stereotype.Service;

import com.example.web_seguro.model.Empresa;
import com.example.web_seguro.model.Maquinarias;
import com.example.web_seguro.model.TipoMaquinaria;
import com.example.web_seguro.repository.EmpresaRepository;
import com.example.web_seguro.repository.MaquinariasRepository;
import com.example.web_seguro.repository.TipoMaquinariaRepository;
import org.springframework.data.domain.Sort;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MaquinariasService {
    
    private final MaquinariasRepository maquinariasRepository; 
    private final TipoMaquinariaRepository tipoMaquinariaRepository; 
    private final EmpresaRepository empresaRepository; 

    public MaquinariasService(MaquinariasRepository maquinariasRepository, 
        TipoMaquinariaRepository tipoMaquinariaRepository, 
        EmpresaRepository empresaRepository)
    {
        this.maquinariasRepository = maquinariasRepository; 
        this.tipoMaquinariaRepository = tipoMaquinariaRepository; 
        this.empresaRepository = empresaRepository; 
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
    public Maquinarias guardarMaquinariaRel(Maquinarias maq, Long tipoMaquinariaId, Long empresaId)
    {
        Empresa empresa = empresaRepository.findById(empresaId)
            .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada (id=" + empresaId + ")"));

        TipoMaquinaria tipo = tipoMaquinariaRepository.findById(tipoMaquinariaId)
            .orElseThrow(() -> new IllegalArgumentException("Tipo de Maquinaria no encontrada (id=" +tipoMaquinariaId + ")"));
    
        if(maq.getUuid() == null || maq.getUuid().isBlank()){
            maq.setUuid(UUID.randomUUID().toString());
        }

        maq.setEmpresa(empresa);
        maq.setTiposMaquinarias(tipo); 
        return maquinariasRepository.save(maq); 

    }

    @Transactional
    public Maquinarias actualizarMaquinaria(String uuid, Maquinarias maq)
    {
        Maquinarias existente = maquinariasRepository.findByUuid(uuid)
            .orElseThrow(() -> new IllegalArgumentException("Maquinaria no encontrada (Uuid=" +uuid + ")"));
        
        existente.setDescripcion(maq.getDescripcion());
        existente.setAnioFabricacion(maq.getAnioFabricacion());
        existente.setCapacidad(maq.getCapacidad());
        existente.setMantencion(maq.getMantencion());
        existente.setCondicionArriendo(maq.getCondicionArriendo());
        existente.setMediosPago(maq.getMediosPago());
        existente.setFechaDisponible(maq.getFechaDisponible());
        existente.setPatente(maq.getPatente());
        existente.setDisponible(maq.getDisponible()); 
        existente.setFotoA(maq.getFotoA());
        existente.setFotoB(maq.getFotoB());
        existente.setFotoC(maq.getFotoC());

        if (maq.getTiposMaquinarias() != null && maq.getTiposMaquinarias().getId() != null) {
            TipoMaquinaria tipo = tipoMaquinariaRepository.findById(maq.getTiposMaquinarias().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Tipo de Maquinaria no encontrada"));
            existente.setTiposMaquinarias(tipo);
        }

        if (maq.getEmpresa() != null && maq.getEmpresa().getId() != null) {
            Empresa empresa = empresaRepository.findById(maq.getEmpresa().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada"));
            existente.setEmpresa(empresa);
        }

        return maquinariasRepository.save(existente); 
    }

    @Transactional
    public void eliminarMaquinaria(String uuid){
        if(!maquinariasRepository.existsByUuid(uuid)){
            throw new IllegalArgumentException("La maquinaria no existe (Uuid=" + uuid + ")"); 
        }
        maquinariasRepository.deleteByUuid(uuid);
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

    
    public List<Maquinarias> listarMaquinariaPorEmpresa(Long empresaId){
        Empresa empresa = empresaRepository.findById(empresaId)
            .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada (id=" +empresaId+ ")"));
        return maquinariasRepository.findByEmpresa(empresa); 
    }
    

    public List<Maquinarias> listaMaquinariasPorTipo(Long tipoId){
        TipoMaquinaria tipo = tipoMaquinariaRepository.findById(tipoId)
            .orElseThrow(()-> new IllegalArgumentException("Tipo de maquinaria no encontrada (id=" +tipoId+ ")"));
        return maquinariasRepository.findByTiposMaquinarias(tipo);  
    }

    public List<Maquinarias> listaMaquinariasDisponibles(){
        return maquinariasRepository.findByDisponible("SI"); 
    }

    public List<TipoMaquinaria> listaTipoMaquinarias() {
        return tipoMaquinariaRepository.findAll(Sort.by("descripcion").ascending());
    }

}
