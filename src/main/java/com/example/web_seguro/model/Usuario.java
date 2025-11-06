package com.example.web_seguro.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Collection;


/**
 * Clase que representa a un usuario.
 * Es el "modelo" de cada objeto de tipo Libro que manejará nuestro
 * microservicio.
 */
@Data // Lombok: genera automáticamente todos los getters, setters, toString(),
      // equals(), hashCode()
@AllArgsConstructor // Lombok: genera un constructor con todos los atributos como parámetros
@NoArgsConstructor // Lombok: genera un constructor sin parámetros (vacío)
@JsonPropertyOrder({ "id", "uuid", "nombres", "primer_apellido", "segundo_apellido","correo","password", "rol" }) // orden de json
@Entity
@Table( name="G7_USUARIOS" )
public class Usuario implements UserDetails {

    // indetificador
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) 
    @Column(name = "ID", unique = true, nullable = false)
    private Long id;

    // identificador unico universal
    @Column(name = "UUID", length = 36, nullable = false, unique = true)
    private String uuid;
    
    // nombre del usuario
    @NotBlank(message = "Nombre no puede estar vacío")
    @Size(min = 1, max = 75, message = "Nombre debe tener entre 1 y 75 caracteres")
    @Column(name = "NOMBRES", length = 75, nullable = false)
    private String nombres;

    // apellido paterno
    @NotBlank(message = "Apellido no puede estar vacío")
    @Size(min = 1, max = 75, message = "Apellido debe tener entre 1 y 75 caracteres")
    @Column(name = "PRIMER_APELLIDO", length = 75, nullable = false)
    private String primerApellido;

    // apellido materno
    @NotBlank(message = "Apellido no puede estar vacío")
    @Size(min = 1, max = 75, message = "Apellido debe tener entre 1 y 75 caracteres")
    @Column(name = "SEGUNDO_APELLIDO", length = 75, nullable = false)
    private String segundoApellido;

    // correo usuario
    @NotBlank(message = "Nombre no puede estar vacío")
    @Size(min = 1, max = 75, message = "Nombre debe tener entre 1 y 75 caracteres")
    @Column(name = "EMAIL", length = 250, unique = true,nullable = false)
    private String email;

    // contraseña
    @NotBlank(message = "Password no puede estar vacío")
    @Size(min = 1, max = 75, message = "Password debe tener entre 1 y 75 caracteres")
    @Column(name = "PASSWORD", length = 250, nullable = false)
    private String password;
    
    // rol
    //@NotBlank(message = "Rol no puede estar vacío")
    @Size(min = 1, max = 75, message = "Rol debe tener entre 1 y 255 caracteres")
    @Column(name = "ROL", length = 50, nullable = false)
    private String rol;


    // --- Implementación de UserDetails ---
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + rol));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email; // usamos email como username
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


}
