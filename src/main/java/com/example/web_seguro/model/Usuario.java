package com.example.web_seguro.model;

import jakarta.persistence.*;

@Entity
@Table( name="USUARIOS" )
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Oracle 12c+ soporta IDENTITY
    @Column(name = "ID")
    private Long id;

    @Column(name = "UUID", length = 36)
    private String uuid;

    @Column(name = "NOMBRES", length = 75)
    private String nombres;

    @Column(name = "PRIMER_APELLIDO", length = 75)
    private String primerApellido;

    @Column(name = "SEGUNDO_APELLIDO", length = 75)
    private String segundoApellido;

    @Column(name = "CORREO", length = 250)
    private String correo;

    @Column(name = "PASSWORD", length = 250)
    private String password;

    // ======== Getters y Setters ========

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getPrimerApellido() { return primerApellido; }
    public void setPrimerApellido(String primerApellido) { this.primerApellido = primerApellido; }

    public String getSegundoApellido() { return segundoApellido; }
    public void setSegundoApellido(String segundoApellido) { this.segundoApellido = segundoApellido; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
