package com.example.web_seguro.config;

import com.example.web_seguro.model.Empresa;
import com.example.web_seguro.model.TipoCultivo;
import com.example.web_seguro.model.Usuario;
import com.example.web_seguro.repository.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.CommandLineRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class DataInitializerTest {

    @Mock
    private EmpresaRepository empresaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private TipoCultivoRepository tipoCultivoRepository;

    private DataInitializer dataInitializer;

    @BeforeEach
    void setUp() {
        dataInitializer = new DataInitializer();
    }

     @Test
    void testInitDatabaseCallsInitializationMethods() throws Exception {
        // 1. Aquí creo mocks de los repositorios
        UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
      //  EmpresaRepository empresaRepository = mock(EmpresaRepository.class);
        TipoCultivoRepository tipoCultivoRepository = mock(TipoCultivoRepository.class);
        TipoMaquinariaRepository tipoMaquinariaRepository = mock(TipoMaquinariaRepository.class);

        // 2. Simulo que todo está vacío (para que el inicializador inserte datos)
        when(usuarioRepository.count()).thenReturn(0L);
        when(tipoCultivoRepository.count()).thenReturn(0L);
      //  when(empresaRepository.count()).thenReturn(0L);
        when(tipoMaquinariaRepository.count()).thenReturn(0L);

        // 3. Instancio mi clase a probar
        DataInitializer initializer = new DataInitializer();

        // 4. Obtengo el CommandLineRunner que ejecuta la carga de datos
        CommandLineRunner runner = initializer.initDatabase(
                usuarioRepository,
                empresaRepository,
                tipoCultivoRepository,
                tipoMaquinariaRepository
        );

        // 5. Ejecuto el runner como lo haría Spring Boot
        runner.run(new String[]{});

        // 6. Verifico que cada repositorio fue usado al menos 1 vez
        verify(usuarioRepository, atLeastOnce()).saveAll(Mockito.anyList());
        verify(tipoCultivoRepository, atLeastOnce()).saveAll(Mockito.anyList());
     //   verify(empresaRepository, atLeastOnce()).save(Mockito.any());
        verify(tipoMaquinariaRepository, atLeastOnce()).save(Mockito.any());
    }

    @Test
void testInicializarEmpresas_CreaEmpresas() {
    // mock empresaRepository.count() → tabla vacía
    when(empresaRepository.count()).thenReturn(0L);

    // mock usuario admin
    Usuario admin = new Usuario();
    when(usuarioRepository.findByNombres("Admin"))
            .thenReturn(Optional.of(admin));

    // mock tipo cultivo trigo
    TipoCultivo trigo = new TipoCultivo();
    when(tipoCultivoRepository.findByDescripcion("Trigo"))
            .thenReturn(Optional.of(trigo));

    // ejecutar
    dataInitializer.inicializarEmpresas(
            empresaRepository, usuarioRepository, tipoCultivoRepository);

    // verificar 2 saves
    verify(empresaRepository, times(2)).save(any(Empresa.class));
}
@Test
void testInicializarEmpresas_NoAdmin_NoCreaNada() {

    when(empresaRepository.count()).thenReturn(0L);
    when(usuarioRepository.findByNombres("Admin"))
            .thenReturn(Optional.empty());  // <-- fuerza el error

    dataInitializer.inicializarEmpresas(empresaRepository, usuarioRepository, tipoCultivoRepository);

    verify(empresaRepository, never()).save(any());
}
@Test
void testInicializarEmpresas_NoTrigo_NoCreaNada() {

    when(empresaRepository.count()).thenReturn(0L);

    Usuario admin = new Usuario();
    when(usuarioRepository.findByNombres("Admin"))
            .thenReturn(Optional.of(admin));

    when(tipoCultivoRepository.findByDescripcion("Trigo"))
            .thenReturn(Optional.empty());  // <-- fuerza camino de error

    dataInitializer.inicializarEmpresas(empresaRepository, usuarioRepository, tipoCultivoRepository);

    verify(empresaRepository, never()).save(any());
}

}