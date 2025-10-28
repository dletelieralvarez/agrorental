package com.example.web_seguro.config;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;
import java.util.UUID;

import com.example.web_seguro.model.Usuario;
import com.example.web_seguro.repository.UsuarioRepository;

/*
 * Definición: @Configuration En este caso específico con DataInitializer, 
 * la anotación @Configuration indica a Spring que esta clase 
 * contiene definiciones de beans (en este caso, un CommandLineRunner)
 * que deben ser procesadas cuando se inicia la aplicación.
 */
@Slf4j
//@Configuration
public class DataInitializer {

    // este codigo se ejecuta automaticamente al arrancar la aplicación
    // verifica si hay datos para no duplicar
    // si no hay datos crea algunos de prueba
    // similar a seeders de laravel, pero más sencillo
    @Bean
    CommandLineRunner initDatabase(
            UsuarioRepository usuarioRepository) {
        return args -> {
            // Solo inserta datos si la tabla está vacía
            if (usuarioRepository.count() == 0) {
                log.info("Inicializando base de datos...");

                List<Usuario> users = Arrays.asList(
                        // Spring Boot
                        new Usuario(
                                null,
                                UUID.randomUUID().toString(),
                                "Pedro",
                                "González",
                                "López",
                                "pedro@duocuc.cl",
                                "123456"),
                        new Usuario(
                                null,
                                UUID.randomUUID().toString(),
                                "Juan",
                                "Pérez",
                                "Rojas",
                                "juan@duocuc.cl",
                                "123456"),
                        new Usuario(
                                null,
                                UUID.randomUUID().toString(),
                                "Diego",
                                "Soto",
                                "Muñoz",
                                "diego@duocuc.cl",
                                "123456")

                );

                usuarioRepository.saveAll(users);
                log.info("✅ {} usuarios insertados correctamente", users.size());
            } else {
                log.info("La base de datos ya contiene datos. Saltando inicialización.");
            }
        };
    }
}