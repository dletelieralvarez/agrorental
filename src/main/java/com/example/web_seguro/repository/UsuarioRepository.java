package com.example.web_seguro.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.web_seguro.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
            Optional<Usuario> findByNombres(String nombres);
            Optional<Usuario> findByEmail(String email);
}
