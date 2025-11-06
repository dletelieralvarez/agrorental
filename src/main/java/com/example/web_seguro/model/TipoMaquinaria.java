package com.example.web_seguro.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
    name="g7_tipos_maquinarias"
)
public class TipoMaquinaria {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) 
    @Column(name = "ID", unique = true, nullable = false)
    private Long id;

    // identificador unico universal
    @Column(name = "UUID", length = 36, nullable = false, unique = true)
    private String uuid;

    @NotBlank(message = "Descripción no puede estar vacía")
    @Size(min = 2, max = 250, message = "Descripción debe tener entre 2 y 250 caracteres")
    @Column(name = "descripcion", nullable = false, length = 250)
    private String descripcion;

}
