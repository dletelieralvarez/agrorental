package com.example.web_seguro.config;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.web_seguro.model.Empresa;
import com.example.web_seguro.model.TipoCultivo;
import com.example.web_seguro.model.TipoMaquinaria;
import com.example.web_seguro.model.Usuario;
import com.example.web_seguro.repository.EmpresaRepository;
import com.example.web_seguro.repository.TipoCultivoRepository;
import com.example.web_seguro.repository.TipoMaquinariaRepository;
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
    // este codigo se ejecuta automaticamente al arrancar la aplicación
    // verifica si hay datos para no duplicar
    // si no hay datos crea algunos de prueba
    // similar a seeders de laravel, pero más sencillo
    @Bean
    CommandLineRunner initDatabase(
            UsuarioRepository usuarioRepository,
            EmpresaRepository empresaRepository,
            TipoCultivoRepository tipoCultivoRepository,
            TipoMaquinariaRepository tipoMaquinariaRepository) {

        return args -> {
            inicializarUsuarios(usuarioRepository);
            inicializarTiposCultivo(tipoCultivoRepository);
            inicializarEmpresas(empresaRepository, usuarioRepository, tipoCultivoRepository);
            inicializarTiposMaquinaria(tipoMaquinariaRepository);
        };
    }

    private void inicializarUsuarios(UsuarioRepository usuarioRepository) {

        String testPassword = System.getenv("ADMIN_TEST_PASS");
        if (testPassword == null) {
            log.warn("ADMIN_TEST_PASS no está definida. Usando contraseña temporal generada.");
            testPassword = UUID.randomUUID().toString();  // Contraseña aleatoria temporal
        }


        // Solo inserta datos si la tabla está vacía
        if (usuarioRepository.count() != 0) {
            log.info("ℹ️ La tabla USUARIO ya contiene datos. Saltando inicialización de usuarios.");
            return;
        }

        log.info("✅ Inicializando usuarios de ejemplo...");

        List<Usuario> users = Arrays.asList(
                new Usuario(
                        null,
                        UUID.randomUUID().toString(),
                        "Admin",
                        "Admin",
                        "Admin",
                        "admin@duocuc.cl",
                        encoder.encode(testPassword),
                        "ADMIN"),
                new Usuario(
                        null,
                        UUID.randomUUID().toString(),
                        "Pedro",
                        "González",
                        "López",
                        "pedro@duocuc.cl",
                        encoder.encode(testPassword),
                        "USER"),
                new Usuario(
                        null,
                        UUID.randomUUID().toString(),
                        "Juan",
                        "Pérez",
                        "Rojas",
                        "juan@duocuc.cl",
                        encoder.encode(testPassword),
                        "USER"),
                new Usuario(
                        null,
                        UUID.randomUUID().toString(),
                        "Diego",
                        "Soto",
                        "Muñoz",
                        "diego@duocuc.cl",
                        encoder.encode(testPassword),
                        "USER")
        );

        usuarioRepository.saveAll(users);
        log.info("✅ {} usuarios insertados correctamente", users.size());
    }

    private void inicializarTiposCultivo(TipoCultivoRepository tipoCultivoRepository) {
        if (tipoCultivoRepository.count() != 0) {
            log.info("ℹ️ Tipos de cultivo ya existen. Saltando inicialización.");
            return;
        }

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
                create("Vides viníferas")
        ));

        log.info("✅ Tipos de cultivos iniciales insertados correctamente.");
    }

    private void inicializarEmpresas(EmpresaRepository empresaRepository,
                                     UsuarioRepository usuarioRepository,
                                     TipoCultivoRepository tipoCultivoRepository) {

        if (empresaRepository.count() != 0) {
            log.info("ℹ️ Empresas ya existen. Saltando inicialización de empresas.");
            return;
        }

        log.info("✅ Inicializando empresas de ejemplo...");

        // Buscar usuario Admin existente
        Optional<Usuario> adminOpt = usuarioRepository.findByNombres("Admin");
        if (adminOpt.isEmpty()) {
            log.error("❌ No se encontró el usuario Admin existente. No se pueden crear empresas.");
            return;
        }
        Usuario admin = adminOpt.get();

        // Buscar tipo de cultivo Trigo existente
        Optional<TipoCultivo> trigoOpt = tipoCultivoRepository.findByDescripcion("Trigo");
        if (trigoOpt.isEmpty()) {
            log.error("❌ No se encontró el tipo de cultivo Trigo existente. No se pueden crear empresas.");
            return;
        }
        TipoCultivo trigo = trigoOpt.get();

        // Empresa A prueba
        Empresa empresaA = new Empresa();
        empresaA.setUuid(UUID.randomUUID().toString());
        empresaA.setRazonSocial("Empresa A Limitada");
        empresaA.setDireccion("Av. Siempre Viva 123");
        empresaA.setTelefono("+56 9 1234 5678");
        empresaA.setNota("Empresa dedicada al cultivo de hortalizas");
        empresaA.setUsuario(admin);
        empresaA.setTipoCultivo(trigo);
        empresaRepository.save(empresaA);

        // Empresa B prueba
        Empresa empresaB = new Empresa();
        empresaB.setUuid(UUID.randomUUID().toString());
        empresaB.setRazonSocial("Empresa B SpA");
        empresaB.setDireccion("Calle Los Olivos 456");
        empresaB.setTelefono("+56 9 8765 4321");
        empresaB.setNota("Empresa especializada en frutales");
        empresaB.setUsuario(admin);
        empresaB.setTipoCultivo(trigo);
        empresaRepository.save(empresaB);

        log.info("✅ Empresas iniciales creadas correctamente.");
    }

    private void inicializarTiposMaquinaria(TipoMaquinariaRepository tipoMaquinariaRepository) {
        if (tipoMaquinariaRepository.count() != 0) {
            log.info("ℹ️ Tipos de maquinaria ya existen. No se insertaron nuevos registros.");
            return;
        }

        List<String> maquinariasBase = List.of(
                "Tractor",
                "Coloso",
                "Bomba de espalda",
                "Cosechadora",
                "Pulverizadora",
                "Sembradora"
        );

        maquinariasBase.forEach(nombre -> {
            TipoMaquinaria maquinaria = new TipoMaquinaria();
            maquinaria.setUuid(UUID.randomUUID().toString());
            maquinaria.setDescripcion(nombre);
            tipoMaquinariaRepository.save(maquinaria);
        });

        log.info("✅ Tipos de maquinaria base insertados correctamente.");
    }

    private TipoCultivo create(String descripcion) {
        TipoCultivo tipo = new TipoCultivo();
        tipo.setDescripcion(descripcion);
        return tipo;
    }
}


 