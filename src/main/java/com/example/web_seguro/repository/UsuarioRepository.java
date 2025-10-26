package com.example.web_seguro.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.web_seguro.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

}
