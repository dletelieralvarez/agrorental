package com.example.web_seguro.service;


import com.example.web_seguro.model.Usuario;
import com.example.web_seguro.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests para CustomUserDetailsService
 * Probamos que Spring Security pueda cargar usuarios correctamente
 */
class CustomUserDetailsServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        // Inicializar los mocks antes de cada test
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void cuandoBuscoUsuarioQueExiste_debeRetornarUsuario() {
        // PREPARAR - Crear un usuario de prueba
        Usuario usuario = new Usuario();
        usuario.setEmail("juan@test.com");
        usuario.setPassword("password123");
        usuario.setRol("USER");

        // Simular que el repositorio encuentra al usuario
        when(usuarioRepository.findByEmail("juan@test.com"))
            .thenReturn(Optional.of(usuario));

        // EJECUTAR - Buscar el usuario
        UserDetails resultado = customUserDetailsService.loadUserByUsername("juan@test.com");

        // VERIFICAR
        assertNotNull(resultado); // Que no sea null
        assertEquals("juan@test.com", resultado.getUsername()); // que el email sea correcto
        assertEquals("password123", resultado.getPassword()); // que la contraseña sea correcta
    }

    @Test
    void cuandoBuscoUsuarioQueNoExiste_debeLanzarError() {
        // PREPARAR - Simular que NO encuentra al usuario
        when(usuarioRepository.findByEmail("noexiste@test.com"))
            .thenReturn(Optional.empty());

        // EJECUTAR Y VERIFICAR - Debe lanzar excepción
        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername("noexiste@test.com");
        });
    }

    @Test
    void cuandoUsuarioTieneRolUser_debeCrearRoleUser() {
        // PREPARAR
        Usuario usuario = new Usuario();
        usuario.setEmail("user@test.com");
        usuario.setPassword("pass123");
        usuario.setRol("USER"); // Rol USER

        when(usuarioRepository.findByEmail("user@test.com"))
            .thenReturn(Optional.of(usuario));

        // EJECUTAR
        UserDetails resultado = customUserDetailsService.loadUserByUsername("user@test.com");

        // VERIFICAR - Debe tener el rol ROLE_USER
        String rolObtenido = resultado.getAuthorities().iterator().next().getAuthority();
        assertEquals("ROLE_USER", rolObtenido);
    }

    @Test
    void cuandoUsuarioTieneRolAdmin_debeCrearRoleAdmin() {
        // PREPARAR
        Usuario usuario = new Usuario();
        usuario.setEmail("admin@test.com");
        usuario.setPassword("pass123");
        usuario.setRol("ADMIN"); // Rol ADMIN

        when(usuarioRepository.findByEmail("admin@test.com"))
            .thenReturn(Optional.of(usuario));

        // EJECUTAR
        UserDetails resultado = customUserDetailsService.loadUserByUsername("admin@test.com");

        // VERIFICAR - Debe tener el rol ROLE_ADMIN
        String rolObtenido = resultado.getAuthorities().iterator().next().getAuthority();
        assertEquals("ROLE_ADMIN", rolObtenido);
    }

    @Test
    void cuentaDebeEstarActivaYNoExpirada() {
        // PREPARAR
        Usuario usuario = new Usuario();
        usuario.setEmail("test@test.com");
        usuario.setPassword("pass123");
        usuario.setRol("USER");

        when(usuarioRepository.findByEmail("test@test.com"))
            .thenReturn(Optional.of(usuario));

        // EJECUTAR
        UserDetails resultado = customUserDetailsService.loadUserByUsername("test@test.com");

        // VERIFICAR - La cuenta debe estar activa y no expirada
        assertTrue(resultado.isEnabled()); // Está habilitada
        assertTrue(resultado.isAccountNonExpired()); // No está expirada
        assertTrue(resultado.isAccountNonLocked()); // No está bloqueada
        assertTrue(resultado.isCredentialsNonExpired()); // Credenciales no expiradas
    }
}
