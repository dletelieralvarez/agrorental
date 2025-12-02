package com.example.web_seguro.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.web_seguro.model.TipoMaquinaria;
import java.util.Optional;

@Repository
public interface TipoMaquinariaRepository extends JpaRepository<TipoMaquinaria, Long> {
    Optional<TipoMaquinaria> findByuuid(String uuid); 
    boolean existsByUuid(String uuid);
    void deleteByUuid(String uuid);
}
