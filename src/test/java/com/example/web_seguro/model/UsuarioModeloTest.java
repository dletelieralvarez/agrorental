package com.example.web_seguro.model;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {

    @Test
    void testGetAuthorities() {
        Usuario usuario = new Usuario();
        usuario.setRol("ADMIN");

        Collection<? extends GrantedAuthority> authorities = usuario.getAuthorities();

        assertEquals(1, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    void testGetPassword() {
        Usuario usuario = new Usuario();
        usuario.setPassword("123456");

        assertEquals("123456", usuario.getPassword());
    }

    @Test
    void testGetUsername() {
        Usuario usuario = new Usuario();
        usuario.setEmail("test@example.com");

        assertEquals("test@example.com", usuario.getUsername());
    }

    @Test
    void testIsAccountNonExpired() {
        Usuario usuario = new Usuario();
        assertTrue(usuario.isAccountNonExpired());
    }

    @Test
    void testIsAccountNonLocked() {
        Usuario usuario = new Usuario();
        assertTrue(usuario.isAccountNonLocked());
    }

    @Test
    void testIsCredentialsNonExpired() {
        Usuario usuario = new Usuario();
        assertTrue(usuario.isCredentialsNonExpired());
    }

    @Test
    void testIsEnabled() {
        Usuario usuario = new Usuario();
        assertTrue(usuario.isEnabled());
    }
}
