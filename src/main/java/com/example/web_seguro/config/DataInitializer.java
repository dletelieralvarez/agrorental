package com.example.web_seguro.config;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;
import java.util.UUID;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.web_seguro.model.TipoCultivo;
import com.example.web_seguro.model.Usuario;
import com.example.web_seguro.repository.TipoCultivoRepository;
import com.example.web_seguro.repository.UsuarioRepository;

/*
 * Definición: @Configuration En este caso específico con DataInitializer, 
 * la anotación @Configuration indica a Spring que esta clase 
 * contiene definiciones de beans (en este caso, un CommandLineRunner)
 * que deben ser procesadas cuando se inicia la aplicación.
 */
@Slf4j
@Configuration
public class DataInitializer {

    // Declarar el encoder
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // este codigo se ejecuta automaticamente al arrancar la aplicación
    // verifica si hay datos para no duplicar
    // si no hay datos crea algunos de prueba
    // similar a seeders de laravel, pero más sencillo
    @Bean
    CommandLineRunner initDatabase(
            UsuarioRepository usuarioRepository,
            TipoCultivoRepository tipoCultivoRepository) {
        return args -> {
            // Solo inserta datos si la tabla está vacía
            if (usuarioRepository.count() == 0) {
                log.info("Inicializando base de datos...");

                List<Usuario> users = Arrays.asList(
                        // Spring Boot
                        new Usuario(
                                null,
                                UUID.randomUUID().toString(),
                                "Admin",
                                "Admin",
                                "Admin",
                                "admin@duocuc.cl",
                                encoder.encode("123456"), "ADMIN"),
                        new Usuario(
                                null,
                                UUID.randomUUID().toString(),
                                "Pedro",
                                "González",
                                "López",
                                "pedro@duocuc.cl",
                                encoder.encode("123456"), "USER"),
                        new Usuario(
                                null,
                                UUID.randomUUID().toString(),
                                "Juan",
                                "Pérez",
                                "Rojas",
                                "juan@duocuc.cl",
                                encoder.encode("123456"), "USER"),
                        new Usuario(
                                null,
                                UUID.randomUUID().toString(),
                                "Diego",
                                "Soto",
                                "Muñoz",
                                "diego@duocuc.cl",
                                encoder.encode("123456"),
                                "USER")

                );

                usuarioRepository.saveAll(users);
                log.info("✅ {} usuarios insertados correctamente", users.size());
            } else {
                log.info("La base de datos ya contiene datos. Saltando inicialización.");
            }

            // insertando tipos de cultivo con otra forma, con una función
            if (tipoCultivoRepository.count() == 0) {
                tipoCultivoRepository.saveAll(Arrays.asList(
                        create("Trigo"),
                        create("Maíz"),
                        create("Cebada"),
                        create("Avena"),
                        create("Papa"),
                        create("Tomate"),
                        create("Uva de mesa"),
                        create("Manzana"),
                        create("Cerezo"),
                        create("Durazno"),
                        create("Frutilla"),
                        create("Nogal"),
                        create("Olivo"),
                        create("Arándano"),
                        create("Vides viníferas")));
                System.out.println("✅ Tipos de cultivos iniciales insertados correctamente.");
            }

        };
    }

    private TipoCultivo create(String descripcion) {
        TipoCultivo tipo = new TipoCultivo();
        tipo.setDescripcion(descripcion);
        return tipo;
    }
}