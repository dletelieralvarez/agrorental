package com.example.web_seguro.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
    name="g7_maquinarias"
)
public class Maquinarias {
    
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

    @NotNull(message = "Año de fabricación no puede ser nulo")
    @Column(name = "anio_fabricacion")
    private Integer anioFabricacion;

    @Digits(integer = 10, fraction = 3, message = "Capacidad inválida")
    @Column(name = "capacidad")
    private BigDecimal capacidad;

    @Size(max = 250)
    @Column(name = "mantencion", length = 250)
    private String mantencion;

    @Size(max = 250)
    @Column(name = "condicion_arriendo", length = 250)
    private String condicionArriendo;

    @Size(max = 250)
    @Column(name = "medios_de_pago", length = 250)
    private String mediosPago;

    @Column(name = "fecha_disponible")
    private LocalDate fechaDisponible;

    @Size(max = 10)
    @Column(name = "patente", length = 10)
    private String patente;

    @NotBlank(message = "Disponible no puede estar vacío")
    @Size(max = 2)
    @Column(name = "disponible", length = 2)
    private String disponible;

    //Foreign Keys
    @ManyToOne
    @JoinColumn(name = "tipos_maquinarias_id", nullable = false,
            foreignKey = @ForeignKey(name = "MAQUINARIAS_TIPOS_MAQUINARIAS_FK"))
    private TipoMaquinaria tiposMaquinarias;

    @ManyToOne
    @JoinColumn(name = "empresas_id", nullable = false,
            foreignKey = @ForeignKey(name = "MAQUINARIAS_EMPRESAS_FK"))
    private Empresa empresa;

    @Size(max = 255)
    @Column(name = "foto_a", length = 255)
    private String fotoA;

    @Size(max = 255)
    @Column(name = "foto_b", length = 255)
    private String fotoB;

    @Size(max = 255)
    @Column(name = "foto_c", length = 255)
    private String fotoC;
    
}
