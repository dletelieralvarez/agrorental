package com.example.web_seguro.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import com.example.web_seguro.model.Usuario;
import com.example.web_seguro.repository.UsuarioRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PerfilControllerTest {
@Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private PerfilController perfilController;

    // si usuario existe se cargan los datos al modelo
    @Test
    public void testGetPerfil_UsuarioEncontrado() {
        // ARRANGE: usuario autenticado con ROLE_USER
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "user@test.com",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        // mock de usuario en BD
        Usuario usuario = new Usuario();
        usuario.setEmail("user@test.com");
        usuario.setNombres("Juan");
        usuario.setPrimerApellido("Pérez");
        usuario.setSegundoApellido("Gómez");

        when(usuarioRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(usuario));

        Model model = new ExtendedModelMap();

        // ACT
        String vista = perfilController.getPerfil(null, model);

        // ASSERT
        assertEquals("perfil", vista);      
        assertEquals("user@test.com", model.getAttribute("email"));
        assertEquals("Juan", model.getAttribute("nombres"));
        assertEquals("Pérez", model.getAttribute("paterno"));
        assertEquals("Gómez", model.getAttribute("materno"));
        assertEquals("[ROLE_USER]", model.getAttribute("rol"));
    }

    // si usuario no existe en BD lanzará RuntimeException
    @Test
    public void testGetPerfil_UsuarioNoEncontrado_LanzaExcepcion() {
        // ARRANGE
        Authentication auth = new UsernamePasswordAuthenticationToken("user@test.com", null);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        when(usuarioRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.empty());

        Model model = new ExtendedModelMap();

        // ACT + ASSERT
        assertThrows(RuntimeException.class,
                () -> perfilController.getPerfil(null, model),
                "Debe lanzar RuntimeException cuando el usuario no existe");
    }
}