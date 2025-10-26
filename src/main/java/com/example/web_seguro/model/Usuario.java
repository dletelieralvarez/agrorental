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

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Clase que representa a un usuario.
 * Es el "modelo" de cada objeto de tipo Libro que manejará nuestro
 * microservicio.
 */
@Data // Lombok: genera automáticamente todos los getters, setters, toString(),
      // equals(), hashCode()
@AllArgsConstructor // Lombok: genera un constructor con todos los atributos como parámetros
@NoArgsConstructor // Lombok: genera un constructor sin parámetros (vacío)
@JsonPropertyOrder({ "id", "uuid", "nombres", "primer_apellido", "segundo_apellido","correo","password" }) // orden de json
@Entity
@Table( name="USUARIOS" )
public class Usuario {

    // indetificador
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Oracle 12c+ soporta IDENTITY
    @Column(name = "ID")
    private Long id;

    // identificador unico universal
    @Column(name = "UUID", length = 36)
    private String uuid;
    
    // nombre del usuario
    @NotBlank(message = "Nombre no puede estar vacío")
    @Size(min = 1, max = 75, message = "Nombre debe tener entre 1 y 75 caracteres")
    @Column(name = "NOMBRES", length = 75, nullable = false)
    private String nombres;

    // apellido paterno
    @NotBlank(message = "Apellido no puede estar vacío")
    @Size(min = 1, max = 75, message = "Apellido debe tener entre 1 y 75 caracteres")
    @Column(name = "PRIMER_APELLIDO", length = 75)
    private String primerApellido;

    // apellido materno
    @NotBlank(message = "Apellido no puede estar vacío")
    @Size(min = 1, max = 75, message = "Apellido debe tener entre 1 y 75 caracteres")
    @Column(name = "SEGUNDO_APELLIDO", length = 75)
    private String segundoApellido;

    // correo usuario
    @NotBlank(message = "Nombre no puede estar vacío")
    @Size(min = 1, max = 75, message = "Nombre debe tener entre 1 y 75 caracteres")
    @Column(name = "CORREO", length = 250)
    private String correo;

    // contraseña
    @NotBlank(message = "Password no puede estar vacío")
    @Size(min = 1, max = 75, message = "Password debe tener entre 1 y 75 caracteres")
    @Column(name = "PASSWORD", length = 250)
    private String password;
    
}
