package com.example.web_seguro.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import jakarta.persistence.*;
import java.util.UUID;


/**
 * Clase que representa a un usuario.
 * Es el "modelo" de cada objeto de tipo Empresaque manejará nuestro
 * microservicio.
 */
@Data // Lombok: genera automáticamente todos los getters, setters, toString(),
      // equals(), hashCode()
@AllArgsConstructor // Lombok: genera un constructor con todos los atributos como parámetros
@NoArgsConstructor // Lombok: genera un constructor sin parámetros (vacío)
@JsonPropertyOrder({ "id", "uuid", "razon_social","telefono","nota","usuarios_id","tipos_cultivos_id" }) // orden de json
@Entity
@Table(name = "G7_EMPRESAS")
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 36)
    private String uuid;

    @Column(name = "razon_social", nullable = false, length = 250)
    private String razonSocial;

    @Column(length = 250)
    private String direccion;

    @Column(length = 75)
    private String telefono;

    @Column(length = 250)
    private String nota;


    // Relaciones
    @ManyToOne
    @JoinColumn(name = "USUARIOS_ID", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "TIPOS_CULTIVOS_ID", nullable = false)
    private TipoCultivo tipoCultivo;

    @PrePersist
    public void prePersist() {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
    }

}


