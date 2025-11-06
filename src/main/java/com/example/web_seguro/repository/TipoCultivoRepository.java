package com.example.web_seguro.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.web_seguro.model.TipoCultivo;

@Repository
public interface TipoCultivoRepository extends JpaRepository<TipoCultivo, Long> {
    Optional<TipoCultivo> findByUuid(String uuid); // para buscar por uuid
    List<TipoCultivo> findAllByOrderByDescripcionAsc(); // para ordenar por descripcion
    
}