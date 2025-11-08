package com.example.web_seguro.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.web_seguro.model.Empresa;
import com.example.web_seguro.model.Maquinarias;
import java.util.List;
import java.util.Optional;
import com.example.web_seguro.model.TipoMaquinaria;


@Repository
public interface MaquinariasRepository extends JpaRepository<Maquinarias, Long> {
    
    Optional<Maquinarias> findByUuid(String uuid); 
    List<Maquinarias> findByEmpresa(Empresa empresa); 
    List<Maquinarias> findByTiposMaquinarias(TipoMaquinaria tiposMaquinarias);
    List<Maquinarias> findByDisponible(String disponible); 
}
