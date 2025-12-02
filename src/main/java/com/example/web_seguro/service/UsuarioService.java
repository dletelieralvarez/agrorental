package com.example.web_seguro.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.web_seguro.model.Usuario;
import com.example.web_seguro.repository.UsuarioRepository;
import org.springframework.transaction.annotation.Transactional; 
import java.util.Optional; 

@Service
public class UsuarioService {
     private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public Usuario guardarUsuario(Usuario usuario) {
        // Encriptar la contraseña antes de guardar
        String encodedPassword = passwordEncoder.encode(usuario.getPassword());
        usuario.setPassword(encodedPassword);
        return usuarioRepository.save(usuario);
    }

    public boolean verificarPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Transactional
    public Usuario registrarUsuario(Usuario usuario) {
        Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(usuario.getEmail());
        if (usuarioExistente.isPresent()) {
            throw new IllegalArgumentException("El correo electrónico ya está registrado.");
        }

        if(usuario.getUuid() == null || usuario.getUuid().isBlank()) {
            usuario.setUuid(java.util.UUID.randomUUID().toString());
        }

        if(usuario.getRol() == null || usuario.getRol().isBlank()) {
            usuario.setRol("USER");
        }

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        return usuarioRepository.save(usuario);
    }

}
