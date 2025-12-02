package com.example.web_seguro.service;

import com.example.web_seguro.model.TipoMaquinaria;
import com.example.web_seguro.repository.TipoMaquinariaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests para TipoMaquinariaService
 * Estos tests verifican que cada método del servicio funcione correctamente
 */

class TipoMaquinariaServiceTest {

    // @Mock simula el repositorio (no usa la base de datos)
    @Mock
    private TipoMaquinariaRepository tipoMaquinariaRepository;

    // @InjectMocks crea el servicio e inyecta automáticamente los mocks
    @InjectMocks
    private TipoMaquinariaService tipoMaquinariaService;

    // Este método se ejecuta ANTES de cada test para preparar los mocks
    // esdta la otra opion que se usan en EmpresaServiceTest
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    //  guardarTipoMaquinaria() 

    @Test
    @DisplayName("Guardar tipo maquinaria con uuid - debe guardarlo tal cual")
    void guardarTipoMaquinaria_ConUuid_DebeGuardarCorrectamente() {
        // 1. PREPARAR (Arrange) - Creamos los datos de prueba
        TipoMaquinaria tipoConUuid = new TipoMaquinaria();
        tipoConUuid.setUuid("uuid-existente-123");
        tipoConUuid.setDescripcion("Excavadora");

        // Le decimos al mock que cuando llamen a save(), devuelva el mismo objeto
        when(tipoMaquinariaRepository.save(any(TipoMaquinaria.class)))
            .thenReturn(tipoConUuid);

        // 2. EJECUTAR (Act) - Llamamos al método que queremos probar
        TipoMaquinaria resultado = tipoMaquinariaService.guardarTipoMaquinaria(tipoConUuid);

        // 3. VERIFICAR (Assert) - Comprobamos que todo funcionó bien
        assertNotNull(resultado, "El resultado no debería ser null");
        assertEquals("uuid-existente-123", resultado.getUuid(), "El UUID debe ser el mismo");
        assertEquals("Excavadora", resultado.getDescripcion());
        
        // Verificamos que el método save() fue llamado 1 vez
        verify(tipoMaquinariaRepository, times(1)).save(tipoConUuid);
    }

    @Test
    @DisplayName("Guardar tipo maquinaria sin uuid - debe generar uno automáticamente")
    void guardarTipoMaquinaria_SinUuid_DebeGenerarUuidAutomaticamente() {
        // PREPARAR - Tipo sin UUID
        TipoMaquinaria tipoSinUuid = new TipoMaquinaria();
        tipoSinUuid.setUuid(null); // Sin UUID
        tipoSinUuid.setDescripcion("Grúa");

        when(tipoMaquinariaRepository.save(any(TipoMaquinaria.class)))
            .thenAnswer(invocation -> invocation.getArgument(0)); // Devuelve lo que recibe

        // EJECUTAR
        TipoMaquinaria resultado = tipoMaquinariaService.guardarTipoMaquinaria(tipoSinUuid);

        // VERIFICAR que se generó un UUID
        assertNotNull(resultado.getUuid(), "Debería tener un UUID generado");
        assertFalse(resultado.getUuid().isBlank(), "El UUID no debe estar vacío");
        assertEquals("Grúa", resultado.getDescripcion());
        
        verify(tipoMaquinariaRepository, times(1)).save(tipoSinUuid);
    }

    @Test
    @DisplayName("Guardar tipo maquinaria con uuid vacio- debe generar uno nuevo")
    void guardarTipoMaquinaria_ConUuidVacio_DebeGenerarUuidNuevo() {
        // PREPARAR
        TipoMaquinaria tipoUuidVacio = new TipoMaquinaria();
        tipoUuidVacio.setUuid(""); // UUID vacío
        tipoUuidVacio.setDescripcion("Bulldozer");

        when(tipoMaquinariaRepository.save(any(TipoMaquinaria.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // EJECUTAR
        TipoMaquinaria resultado = tipoMaquinariaService.guardarTipoMaquinaria(tipoUuidVacio);

        // VERIFICAR
        assertNotNull(resultado.getUuid());
        assertFalse(resultado.getUuid().isBlank(), "Debe generar un UUID nuevo");
        
        verify(tipoMaquinariaRepository, times(1)).save(tipoUuidVacio);
    }

    // ========== TESTS PARA eliminarTipoMaquinaria() ==========

    @Test
    @DisplayName("Eliminar tipo maquinaria que existe - debe eliminarse correctamente")
    void eliminarTipoMaquinaria_Existente_DebeEliminarCorrectamente() {
        // PREPARAR
        String uuid = "uuid-a-eliminar";
        
        // Simulamos que el tipo SÍ existe
        when(tipoMaquinariaRepository.existsByUuid(uuid)).thenReturn(true);
        
        // No necesitamos simular deleteByUuid porque no devuelve nada (void)

        // EJECUTAR
        // Este método no lanza excepción si todo va bien
        assertDoesNotThrow(() -> tipoMaquinariaService.eliminarTipoMaquinaria(uuid));

        // VERIFICAR que se llamaron los métodos correctos
        verify(tipoMaquinariaRepository, times(1)).existsByUuid(uuid);
        verify(tipoMaquinariaRepository, times(1)).deleteByUuid(uuid);
    }

    @Test
    @DisplayName("Eliminar tipo maquinaria que noexiste - debe lanzar excepción")
    void eliminarTipoMaquinaria_NoExistente_DebeLanzarExcepcion() {
        // PREPARAR
        String uuidInexistente = "uuid-que-no-existe";
        
        // Simulamos que el tipo NO existe
        when(tipoMaquinariaRepository.existsByUuid(uuidInexistente)).thenReturn(false);

        // EJECUTAR Y VERIFICAR - Esperamos que lance una RuntimeException
        RuntimeException excepcion = assertThrows(
            RuntimeException.class,
            () -> tipoMaquinariaService.eliminarTipoMaquinaria(uuidInexistente),
            "Debería lanzar RuntimeException cuando no existe"
        );

        // Verificamos el mensaje de error
        assertEquals("El tipo de maquinaria no existe", excepcion.getMessage());
        
        // Verificamos que NO se intentó eliminar nada
        verify(tipoMaquinariaRepository, times(1)).existsByUuid(uuidInexistente);
        verify(tipoMaquinariaRepository, never()).deleteByUuid(anyString());
    }

    // TESTS actualizaTipoMaquinaria()

    @Test
    @DisplayName("Actualizar tipo maquinaria EXISTENTE - debe actualizar correctamente")
    void actualizaTipoMaquinaria_Existente_DebeActualizarCorrectamente() {
        // PREPARAR
        String uuid = "uuid-123";
        
        // Tipo original en la BD
        TipoMaquinaria tipoOriginal = new TipoMaquinaria();
        tipoOriginal.setId(1L);
        tipoOriginal.setUuid(uuid);
        tipoOriginal.setDescripcion("Descripción vieja");

        // Nuevos datos para actualizar
        TipoMaquinaria tipoNuevo = new TipoMaquinaria();
        tipoNuevo.setDescripcion("Descripción actualizada");

        // Simulamos que encontramos el tipo original
        when(tipoMaquinariaRepository.findByuuid(uuid))
            .thenReturn(Optional.of(tipoOriginal));
        
        // Simulamos que el save devuelve el tipo actualizado
        when(tipoMaquinariaRepository.save(any(TipoMaquinaria.class)))
            .thenReturn(tipoOriginal);

        // EJECUTAR
        TipoMaquinaria resultado = tipoMaquinariaService.actualizaTipoMaquinaria(uuid, tipoNuevo);

        // VERIFICAR
        assertNotNull(resultado);
        assertEquals("Descripción actualizada", resultado.getDescripcion());
        assertEquals(uuid, resultado.getUuid(), "El UUID no debe cambiar");
        
        verify(tipoMaquinariaRepository, times(1)).findByuuid(uuid);
        verify(tipoMaquinariaRepository, times(1)).save(tipoOriginal);
    }

    @Test
    @DisplayName("Actualizar tipo maquinaria NO EXISTENTE - debe lanzar excepción")
    void actualizaTipoMaquinaria_NoExistente_DebeLanzarExcepcion() {
        // PREPARAR
        String uuidInexistente = "uuid-inexistente";
        TipoMaquinaria tipoNuevo = new TipoMaquinaria();
        tipoNuevo.setDescripcion("Nueva descripción");

        // Simulamos que NO se encuentra el tipo
        when(tipoMaquinariaRepository.findByuuid(uuidInexistente))
            .thenReturn(Optional.empty());

        // EJECUTAR Y VERIFICAR
        RuntimeException excepcion = assertThrows(
            RuntimeException.class,
            () -> tipoMaquinariaService.actualizaTipoMaquinaria(uuidInexistente, tipoNuevo)
        );

        assertEquals("Tipo de maquinaria no encontrada", excepcion.getMessage());
        
        // Verificamos que NO se intentó guardar nada
        verify(tipoMaquinariaRepository, times(1)).findByuuid(uuidInexistente);
        verify(tipoMaquinariaRepository, never()).save(any(TipoMaquinaria.class));
    }

    //  listaTipoMaquinarias() 

    @Test
    @DisplayName("Listar todos los tipos - debe devolver lista completa")
    void listaTipoMaquinarias_DebeRetornarListaCompleta() {
        // PREPARAR - Creamos una lista de ejemplo
        TipoMaquinaria tipo1 = new TipoMaquinaria();
        tipo1.setId(1L);
        tipo1.setDescripcion("Excavadora");

        TipoMaquinaria tipo2 = new TipoMaquinaria();
        tipo2.setId(2L);
        tipo2.setDescripcion("Grúa");

        List<TipoMaquinaria> listaMock = Arrays.asList(tipo1, tipo2);

        when(tipoMaquinariaRepository.findAll()).thenReturn(listaMock);

        // EJECUTAR
        List<TipoMaquinaria> resultado = tipoMaquinariaService.listaTipoMaquinarias();

        // VERIFICAR
        assertNotNull(resultado);
        assertEquals(2, resultado.size(), "Debe retornar 2 elementos");
        assertEquals("Excavadora", resultado.get(0).getDescripcion());
        assertEquals("Grúa", resultado.get(1).getDescripcion());
        
        verify(tipoMaquinariaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Listar todos cuando NO HAY datos - debe devolver lista vacía")
    void listaTipoMaquinarias_SinDatos_DebeRetornarListaVacia() {
        // PREPARAR
        when(tipoMaquinariaRepository.findAll()).thenReturn(Arrays.asList());

        // EJECUTAR
        List<TipoMaquinaria> resultado = tipoMaquinariaService.listaTipoMaquinarias();

        // VERIFICAR
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty(), "La lista debe estar vacía");
        
        verify(tipoMaquinariaRepository, times(1)).findAll();
    }

    //  buscarTipoMaquinariaPorID() 

    @Test
    @DisplayName("Buscar por ID existente - debe encontrar el tipo")
    void buscarTipoMaquinariaPorID_Existente_DebeEncontrar() {
        // PREPARAR
        Long id = 1L;
        TipoMaquinaria tipoMock = new TipoMaquinaria();
        tipoMock.setId(id);
        tipoMock.setDescripcion("Excavadora");

        when(tipoMaquinariaRepository.findById(id))
            .thenReturn(Optional.of(tipoMock));

        // EJECUTAR
        Optional<TipoMaquinaria> resultado = tipoMaquinariaService.buscarTipoMaquinariaPorID(id);

        // VERIFICAR
        assertTrue(resultado.isPresent(), "Debe encontrar el tipo");
        assertEquals(id, resultado.get().getId());
        assertEquals("Excavadora", resultado.get().getDescripcion());
        
        verify(tipoMaquinariaRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Buscar por ID inexistente - debe devolver Optional vacío")
    void buscarTipoMaquinariaPorID_Inexistente_DebeRetornarVacio() {
        // PREPARAR
        Long idInexistente = 999L;
        when(tipoMaquinariaRepository.findById(idInexistente))
            .thenReturn(Optional.empty());

        // EJECUTAR
        Optional<TipoMaquinaria> resultado = tipoMaquinariaService.buscarTipoMaquinariaPorID(idInexistente);

        // VERIFICAR
        assertFalse(resultado.isPresent(), "No debe encontrar nada");
        
        verify(tipoMaquinariaRepository, times(1)).findById(idInexistente);
    }

    // buscarTipoMaquinariaPorUuid() 

    @Test
    @DisplayName("Buscar por UUID existente - debe encontrar el tipo")
    void buscarTipoMaquinariaPorUuid_Existente_DebeEncontrar() {
        // PREPARAR
        String uuid = "uuid-123";
        TipoMaquinaria tipoMock = new TipoMaquinaria();
        tipoMock.setUuid(uuid);
        tipoMock.setDescripcion("Grúa");

        when(tipoMaquinariaRepository.findByuuid(uuid))
            .thenReturn(Optional.of(tipoMock));

        // EJECUTAR
        Optional<TipoMaquinaria> resultado = tipoMaquinariaService.buscarTipoMaquinariaPorUuid(uuid);

        // VERIFICAR
        assertTrue(resultado.isPresent(), "Debe encontrar el tipo");
        assertEquals(uuid, resultado.get().getUuid());
        assertEquals("Grúa", resultado.get().getDescripcion());
        
        verify(tipoMaquinariaRepository, times(1)).findByuuid(uuid);
    }

    @Test
    @DisplayName("Buscar por UUID inexistente - debe devolver Optional vacío")
    void buscarTipoMaquinariaPorUuid_Inexistente_DebeRetornarVacio() {
        // PREPARAR
        String uuidInexistente = "uuid-no-existe";
        when(tipoMaquinariaRepository.findByuuid(uuidInexistente))
            .thenReturn(Optional.empty());

        // EJECUTAR
        Optional<TipoMaquinaria> resultado = tipoMaquinariaService.buscarTipoMaquinariaPorUuid(uuidInexistente);

        // VERIFICAR
        assertFalse(resultado.isPresent(), "No debe encontrar nada");
        
        verify(tipoMaquinariaRepository, times(1)).findByuuid(uuidInexistente);
    }
}