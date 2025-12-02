package com.example.web_seguro.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Sort;


import com.example.web_seguro.model.Maquinarias;
import com.example.web_seguro.model.Empresa;
import com.example.web_seguro.model.TipoMaquinaria;
import com.example.web_seguro.repository.MaquinariasRepository;
import com.example.web_seguro.repository.TipoMaquinariaRepository;
import com.example.web_seguro.repository.EmpresaRepository;

/*
En mis pruebas unitarias uso @Mock para crear versiones falsas 
de los repositorios MaquinariasRepository, TipoMaquinariaRepository
y EmpresaRepository, porque no quiero conectarme a la base de datos
real; así controlo exactamente qué devuelven durante cada test
Luego utilizo @InjectMocks para que Mockito cree una instancia 
real de MaquinariasService pero inyectándole estos repositorios simulados,
permitiéndome probar únicamente la lógica del servicio sin depender de infraestructura externa
Finalmente, en el método setup() ejecuto MockitoAnnotations.openMocks(this) 
para inicializar todos los mocks antes de cada prueba, asegurándome 
de que cada test parta limpio y sin datos residuales.
*/
public class MaquinariasServiceTest {

  @Mock
  private MaquinariasRepository maquinariasRepository;

  // esta anotación para simular el repositorio de maquinarias.
  // No quiero conectarme a la base de datos real, así que Mockito crea un
  // "repositorio falso" donde controlo lo que devuelve.

  @Mock
  private TipoMaquinariaRepository tipoMaquinariaRepository;

  // Aquí simulo el repositorio de tipos de maquinaria para poder probar los
  // métodos sin depender de datos reales.

  @Mock
  private EmpresaRepository empresaRepository;

  // Este mock representa el repositorio de empresas, también falso,
  // y permite definir su comportamiento durante las pruebas.

  @InjectMocks
  private MaquinariasService maquinariasService;

  // Con esta anotación le digo a Mockito que inyecte automáticamente
  // los repositorios simulados dentro del servicio real.
  // Así pruebo la lógica del servicio sin usar infraestructura real.

   @InjectMocks
  private TipoMaquinariaService tipoMaquinariaService;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
    // En este método inicializo manualmente todos los @Mock antes de cada test.
    // De esta forma me aseguro de que cada prueba parte con mocks nuevos,
    // sin estado previo y completamente independientes entre sí.
    // Esta es una forma válida de trabajar con Mockito, pero no es la única:
    // en JUnit 5 también puedo usar @ExtendWith(MockitoExtension.class),
    // como en EmpresaServiceTest, lo cual inicializa los mocks de forma
    // automática y evita la necesidad de llamar a openMocks() en el setup().
  }

  @Test
  void guardarMaquinariaRel_debeGuardarCorrectamente() {
    // ARRANGE
    Maquinarias maq = new Maquinarias();
    maq.setDescripcion("Tractor");

    Empresa empresa = new Empresa();
    empresa.setId(10L);

    TipoMaquinaria tipo = new TipoMaquinaria();
    tipo.setId(20L);

    when(empresaRepository.findById(10L)).thenReturn(Optional.of(empresa));
    when(tipoMaquinariaRepository.findById(20L)).thenReturn(Optional.of(tipo));
    when(maquinariasRepository.save(any(Maquinarias.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    // ACT
    Maquinarias result = maquinariasService.guardarMaquinariaRel(maq, 20L, 10L);

    // ASSERT
    assertNotNull(result.getUuid(), "El UUID debe generarse automáticamente");
    assertEquals(empresa, result.getEmpresa());
    assertEquals(tipo, result.getTiposMaquinarias());
  }

  // guardarMaquinaria() ==========

    @Test
    void guardarMaquinaria_ConUuid_DebeGuardarCorrectamente() {
        // PREPARAR - Maquinaria que YA tiene UUID
        Maquinarias maquinaria = new Maquinarias();
        maquinaria.setUuid("uuid-existente-123");
        maquinaria.setDescripcion("Excavadora");

        // Simular que el repositorio guarda y devuelve la maquinaria
        when(maquinariasRepository.save(any(Maquinarias.class)))
            .thenReturn(maquinaria);

        // EJECUTAR
        Maquinarias resultado = maquinariasService.guardarMaquinaria(maquinaria);

        // VERIFICAR
        assertNotNull(resultado);
        assertEquals("uuid-existente-123", resultado.getUuid(), 
            "El UUID debe ser el mismo que se proporcionó");
        assertEquals("Excavadora", resultado.getDescripcion());
        
        verify(maquinariasRepository, times(1)).save(maquinaria);
    }

    @Test
    void guardarMaquinaria_SinUuid_DebeGenerarUuidAutomaticamente() {
        // PREPARAR - Maquinaria SIN UUID
        Maquinarias maquinaria = new Maquinarias();
        maquinaria.setUuid(null); // UUID es null
        maquinaria.setDescripcion("Grúa");

        // Simular que el repositorio devuelve lo que recibe
        when(maquinariasRepository.save(any(Maquinarias.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // EJECUTAR
        Maquinarias resultado = maquinariasService.guardarMaquinaria(maquinaria);

        // VERIFICAR que se generó un UUID automáticamente
        assertNotNull(resultado.getUuid(), 
            "El UUID debe generarse automáticamente cuando es null");
        assertFalse(resultado.getUuid().isBlank(), 
            "El UUID generado no debe estar vacío");
        assertEquals("Grúa", resultado.getDescripcion());
        
        verify(maquinariasRepository, times(1)).save(maquinaria);
    }

    @Test
    void guardarMaquinaria_ConUuidVacio_DebeGenerarUuidNuevo() {
        // PREPARAR - Maquinaria con UUID vacío
        Maquinarias maquinaria = new Maquinarias();
        maquinaria.setUuid(""); // UUID vacío (solo espacios en blanco)
        maquinaria.setDescripcion("Bulldozer");

        when(maquinariasRepository.save(any(Maquinarias.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // EJECUTAR
        Maquinarias resultado = maquinariasService.guardarMaquinaria(maquinaria);

        // VERIFICAR que generó un UUID nuevo
        assertNotNull(resultado.getUuid());
        assertFalse(resultado.getUuid().isBlank(), 
            "Debe generar un UUID nuevo cuando está vacío");
        assertEquals("Bulldozer", resultado.getDescripcion());
        
        verify(maquinariasRepository, times(1)).save(maquinaria);
    }

    @Test
    void guardarMaquinaria_ConUuidBlank_DebeGenerarUuidNuevo() {
        // PREPARAR - Maquinaria con UUID solo espacios
        Maquinarias maquinaria = new Maquinarias();
        maquinaria.setUuid("   "); // Solo espacios en blanco
        maquinaria.setDescripcion("Retroexcavadora");

        when(maquinariasRepository.save(any(Maquinarias.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // EJECUTAR
        Maquinarias resultado = maquinariasService.guardarMaquinaria(maquinaria);

        // VERIFICAR que generó un UUID nuevo
        assertNotNull(resultado.getUuid());
        assertFalse(resultado.getUuid().isBlank(), 
            "Debe generar un UUID nuevo cuando solo tiene espacios");
        
        verify(maquinariasRepository, times(1)).save(maquinaria);
    }


  @Test
  void guardarMaquinariaRel_debeLanzarExcepcionSiEmpresaNoExiste() {
    // ARRANGE
    Maquinarias maq = new Maquinarias();
    when(empresaRepository.findById(99L)).thenReturn(Optional.empty());

    // ACT + ASSERT
    assertThrows(IllegalArgumentException.class, () -> maquinariasService.guardarMaquinariaRel(maq, 20L, 99L));
  }

  @Test
  void guardarMaquinariaRel_debeLanzarExcepcionSiTipoNoExiste() {
    // ARRANGE
    Maquinarias maq = new Maquinarias();

    Empresa empresa = new Empresa();
    empresa.setId(10L);

    when(empresaRepository.findById(10L)).thenReturn(Optional.of(empresa));
    when(tipoMaquinariaRepository.findById(888L)).thenReturn(Optional.empty());

    // ACT + ASSERT
    assertThrows(IllegalArgumentException.class, () -> maquinariasService.guardarMaquinariaRel(maq, 888L, 10L));
  }

  @Test
  void actualizarMaquinaria_debeActualizarCorrectamente() {
    // ARRANGE
    String uuid = "abc-123";

    Maquinarias existente = new Maquinarias();
    existente.setUuid(uuid);
    existente.setDescripcion("Vieja");

    // NUEVOS DATOS
    Maquinarias input = new Maquinarias();
    input.setDescripcion("Nueva");
    input.setDisponible("SI");

    // Mock de empresa y tipo
    Empresa empresa = new Empresa();
    empresa.setId(10L);
    input.setEmpresa(empresa);

    TipoMaquinaria tipo = new TipoMaquinaria();
    tipo.setId(20L);
    input.setTiposMaquinarias(tipo);

    when(maquinariasRepository.findByUuid(uuid)).thenReturn(Optional.of(existente));
    when(empresaRepository.findById(10L)).thenReturn(Optional.of(empresa));
    when(tipoMaquinariaRepository.findById(20L)).thenReturn(Optional.of(tipo));

    when(maquinariasRepository.save(any(Maquinarias.class)))
        .thenAnswer(inv -> inv.getArgument(0));

    // ACT
    Maquinarias result = maquinariasService.actualizarMaquinaria(uuid, input);

    // ASSERT
    assertEquals("Nueva", result.getDescripcion());
    assertEquals("SI", result.getDisponible());
    assertEquals(empresa, result.getEmpresa());
    assertEquals(tipo, result.getTiposMaquinarias());
  }

  @Test
  void actualizarMaquinaria_debeLanzarExcepcionSiNoExisteUuid() {
    when(maquinariasRepository.findByUuid("no-existe"))
        .thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class,
        () -> maquinariasService.actualizarMaquinaria("no-existe", new Maquinarias()));
  }

  @Test
  void actualizarMaquinaria_debeLanzarExcepcionSiEmpresaNoExiste() {
    String uuid = "abc";

    Maquinarias existente = new Maquinarias();
    existente.setUuid(uuid);

    Maquinarias input = new Maquinarias();
    Empresa emp = new Empresa();
    emp.setId(999L);
    input.setEmpresa(emp);

    when(maquinariasRepository.findByUuid(uuid)).thenReturn(Optional.of(existente));
    when(empresaRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> maquinariasService.actualizarMaquinaria(uuid, input));
  }

  @Test
  void actualizarMaquinaria_debeLanzarExcepcionSiTipoNoExiste() {
    String uuid = "abc";

    Maquinarias existente = new Maquinarias();
    existente.setUuid(uuid);

    Maquinarias input = new Maquinarias();
    TipoMaquinaria tipo = new TipoMaquinaria();
    tipo.setId(555L);
    input.setTiposMaquinarias(tipo);

    when(maquinariasRepository.findByUuid(uuid)).thenReturn(Optional.of(existente));
    when(tipoMaquinariaRepository.findById(555L)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> maquinariasService.actualizarMaquinaria(uuid, input));
  }

  @Test
  void eliminarMaquinaria_debeEliminarCorrectamente() {
    String uuid = "abc-123";

    when(maquinariasRepository.existsByUuid(uuid)).thenReturn(true);

    // ACT
    maquinariasService.eliminarMaquinaria(uuid);

    // ASSERT
    verify(maquinariasRepository, times(1)).deleteByUuid(uuid);
  }

  @Test
  void eliminarMaquinaria_debeLanzarExcepcionSiNoExiste() {
    when(maquinariasRepository.existsByUuid("nope")).thenReturn(false);

    assertThrows(IllegalArgumentException.class, () -> maquinariasService.eliminarMaquinaria("nope"));
  }

  @Test
  void listaMaquinariasDisponibles_debeRetornarSoloDisponibles() {
    Maquinarias m1 = new Maquinarias();
    m1.setDisponible("SI");
    Maquinarias m2 = new Maquinarias();
    m2.setDisponible("SI");

    when(maquinariasRepository.findByDisponible("SI"))
        .thenReturn(List.of(m1, m2));

    List<Maquinarias> result = maquinariasService.listaMaquinariasDisponibles();

    assertEquals(2, result.size());
    assertTrue(result.stream().allMatch(m -> "SI".equals(m.getDisponible())));
  }

  @Test
  void listarMaquinariaPorEmpresa_debeRetornarListaCorrecta() {
    Empresa emp = new Empresa();
    emp.setId(10L);

    Maquinarias m1 = new Maquinarias();
    m1.setEmpresa(emp);
    Maquinarias m2 = new Maquinarias();
    m2.setEmpresa(emp);

    when(empresaRepository.findById(10L)).thenReturn(Optional.of(emp));
    when(maquinariasRepository.findByEmpresa(emp)).thenReturn(List.of(m1, m2));

    List<Maquinarias> result = maquinariasService.listarMaquinariaPorEmpresa(10L);

    assertEquals(2, result.size());
  }

  @Test
  void listarMaquinariaPorEmpresa_debeLanzarExcepcionSiNoExisteEmpresa() {
    when(empresaRepository.findById(77L)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> maquinariasService.listarMaquinariaPorEmpresa(77L));
  }

  // test lista maqiuonarioas por tipo

  @Test
void listaMaquinariasPorTipo_TipoExiste_DebeRetornarListaDeMaquinarias() {
    // PREPARAR
    Long tipoId = 1L;
    
    TipoMaquinaria tipo = new TipoMaquinaria();
    tipo.setId(tipoId);
    tipo.setDescripcion("Excavadoras");

    Maquinarias maq1 = new Maquinarias();
    maq1.setDescripcion("Excavadora 1");
    
    Maquinarias maq2 = new Maquinarias();
    maq2.setDescripcion("Excavadora 2");

    List<Maquinarias> listaMaquinarias = Arrays.asList(maq1, maq2);

    when(tipoMaquinariaRepository.findById(tipoId))
        .thenReturn(Optional.of(tipo));
    when(maquinariasRepository.findByTiposMaquinarias(tipo))
        .thenReturn(listaMaquinarias);

    // EJECUTAR
    List<Maquinarias> resultado = maquinariasService.listaMaquinariasPorTipo(tipoId);

    // VERIFICAR
    assertNotNull(resultado);
    assertEquals(2, resultado.size());
    assertEquals("Excavadora 1", resultado.get(0).getDescripcion());
    assertEquals("Excavadora 2", resultado.get(1).getDescripcion());
    
    verify(tipoMaquinariaRepository, times(1)).findById(tipoId);
    verify(maquinariasRepository, times(1)).findByTiposMaquinarias(tipo);
}

@Test
void listaMaquinariasPorTipo_TipoNoExiste_DebeLanzarExcepcion() {
    // PREPARAR
    Long tipoIdInexistente = 999L;
    
    when(tipoMaquinariaRepository.findById(tipoIdInexistente))
        .thenReturn(Optional.empty());

    // EJECUTAR Y VERIFICAR
    IllegalArgumentException excepcion = assertThrows(
        IllegalArgumentException.class,
        () -> maquinariasService.listaMaquinariasPorTipo(tipoIdInexistente)
    );

    assertTrue(excepcion.getMessage().contains("Tipo de maquinaria no encontrada"));
    assertTrue(excepcion.getMessage().contains("999"));
    
    verify(tipoMaquinariaRepository, times(1)).findById(tipoIdInexistente);
    verify(maquinariasRepository, never()).findByTiposMaquinarias(any());
}

@Test
void listaMaquinariasPorTipo_TipoSinMaquinarias_DebeRetornarListaVacia() {
    // PREPARAR
    Long tipoId = 5L;
    
    TipoMaquinaria tipo = new TipoMaquinaria();
    tipo.setId(tipoId);
    tipo.setDescripcion("Tipo sin maquinarias");

    when(tipoMaquinariaRepository.findById(tipoId))
        .thenReturn(Optional.of(tipo));
    when(maquinariasRepository.findByTiposMaquinarias(tipo))
        .thenReturn(Arrays.asList()); // Lista vacía

    // EJECUTAR
    List<Maquinarias> resultado = maquinariasService.listaMaquinariasPorTipo(tipoId);

    // VERIFICAR
    assertNotNull(resultado);
    assertTrue(resultado.isEmpty());
    
    verify(tipoMaquinariaRepository, times(1)).findById(tipoId);
    verify(maquinariasRepository, times(1)).findByTiposMaquinarias(tipo);
}

// liusta de maquinarias
@Test
void listaMaquinarias_DebeRetornarTodasLasMaquinarias() {
    // PREPARAR
    Maquinarias maq1 = new Maquinarias();
    maq1.setDescripcion("Excavadora");

    Maquinarias maq2 = new Maquinarias();
    maq2.setDescripcion("Grúa");

    Maquinarias maq3 = new Maquinarias();
    maq3.setDescripcion("Bulldozer");

    List<Maquinarias> listaMaquinarias = Arrays.asList(maq1, maq2, maq3);

    when(maquinariasRepository.findAll()).thenReturn(listaMaquinarias);

    // EJECUTAR
    List<Maquinarias> resultado = maquinariasService.listaMaquinarias();

    // VERIFICAR
    assertNotNull(resultado);
    assertEquals(3, resultado.size());
    assertEquals("Excavadora", resultado.get(0).getDescripcion());
    assertEquals("Grúa", resultado.get(1).getDescripcion());
    assertEquals("Bulldozer", resultado.get(2).getDescripcion());
    
    verify(maquinariasRepository, times(1)).findAll();
}

@Test
void listaMaquinarias_SinDatos_DebeRetornarListaVacia() {
    // PREPARAR
    when(maquinariasRepository.findAll()).thenReturn(Arrays.asList());

    // EJECUTAR
    List<Maquinarias> resultado = maquinariasService.listaMaquinarias();

    // VERIFICAR
    assertNotNull(resultado);
    assertTrue(resultado.isEmpty());
    
    verify(maquinariasRepository, times(1)).findAll();
}

// busca maquinarias por id

@Test
void buscaMaquinariaPorId_Existente_DebeRetornarMaquinaria() {
    // PREPARAR
    Long id = 1L;
    
    Maquinarias maquinaria = new Maquinarias();
    maquinaria.setId(id);
    maquinaria.setDescripcion("Excavadora");

    when(maquinariasRepository.findById(id))
        .thenReturn(Optional.of(maquinaria));

    // EJECUTAR
    Optional<Maquinarias> resultado = maquinariasService.buscaMaquinariaPorId(id);

    // VERIFICAR
    assertTrue(resultado.isPresent());
    assertEquals(id, resultado.get().getId());
    assertEquals("Excavadora", resultado.get().getDescripcion());
    
    verify(maquinariasRepository, times(1)).findById(id);
}

@Test
void buscaMaquinariaPorId_NoExistente_DebeRetornarVacio() {
    // PREPARAR
    Long idInexistente = 999L;
    
    when(maquinariasRepository.findById(idInexistente))
        .thenReturn(Optional.empty());

    // EJECUTAR
    Optional<Maquinarias> resultado = maquinariasService.buscaMaquinariaPorId(idInexistente);

    // VERIFICAR
    assertFalse(resultado.isPresent());
    
    verify(maquinariasRepository, times(1)).findById(idInexistente);
}

// busca maquinarias por uuid


@Test
void buscaMaquinariaPorUuid_Existente_DebeRetornarMaquinaria() {
    // PREPARAR
    String uuid = "uuid-123";
    
    Maquinarias maquinaria = new Maquinarias();
    maquinaria.setUuid(uuid);
    maquinaria.setDescripcion("Excavadora");

    when(maquinariasRepository.findByUuid(uuid))
        .thenReturn(Optional.of(maquinaria));

    // EJECUTAR
    Optional<Maquinarias> resultado = maquinariasService.buscaMaquinariaPorUuid(uuid);

    // VERIFICAR
    assertTrue(resultado.isPresent());
    assertEquals(uuid, resultado.get().getUuid());
    assertEquals("Excavadora", resultado.get().getDescripcion());
    
    verify(maquinariasRepository, times(1)).findByUuid(uuid);
}

@Test
void buscaMaquinariaPorUuid_NoExistente_DebeRetornarVacio() {
    // PREPARAR
    String uuidInexistente = "uuid-no-existe";
    
    when(maquinariasRepository.findByUuid(uuidInexistente))
        .thenReturn(Optional.empty());

    // EJECUTAR
    Optional<Maquinarias> resultado = maquinariasService.buscaMaquinariaPorUuid(uuidInexistente);

    // VERIFICAR
    assertFalse(resultado.isPresent());
    
    verify(maquinariasRepository, times(1)).findByUuid(uuidInexistente);
}



}
