package com.example.web_seguro.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.web_seguro.model.Empresa;

import org.springframework.stereotype.Repository;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long>{
    Optional<Empresa> findByUuid(String uuid); // para buscar por uuid
    List<Empresa> findAllByOrderByRazonSocialAsc(); // para ordenar por razon social

    // buscar empresas del usuario actual
    List<Empresa> findByUsuarioIdOrderByRazonSocialAsc(Long usuarioId);
}
