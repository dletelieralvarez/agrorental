package com.example.web_seguro.controller;

import com.example.web_seguro.model.TipoCultivo;
import com.example.web_seguro.repository.TipoCultivoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TipoCultivoControllerTest {
    
    @Mock
    private TipoCultivoRepository tipoCultivoRepository;

    @InjectMocks
    private TipoCultivoController tipoCultivoController;

    // GET /tiposcultivos
    @Test
    public void testGetTiposCultivos() {
        // ARRANGE
        TipoCultivo c1 = new TipoCultivo();
        TipoCultivo c2 = new TipoCultivo();
        List<TipoCultivo> lista = List.of(c1, c2);

        when(tipoCultivoRepository.findAllByOrderByDescripcionAsc())
                .thenReturn(lista);

        Model model = new ExtendedModelMap();

        // ACT
        String vista = tipoCultivoController.getTiposCultivos(model);

        // ASSERT
        assertEquals("tipo_cultivo", vista);
        assertEquals(lista, model.getAttribute("cultivos"));
    }

    // GET /tiposcultivos/nuevo
    @Test
    public void testMostrarFormularioNuevo() {
        // ARRANGE
        Model model = new ExtendedModelMap();

        // ACT
        String vista = tipoCultivoController.mostrarFormulario(model);

        // ASSERT
        assertEquals("tipocultivo_form", vista);
        assertNotNull(model.getAttribute("tipoCultivo"));
        assertTrue(model.getAttribute("tipoCultivo") instanceof TipoCultivo);
    }

    // POST /tiposcultivos/nuevo
    @Test
    public void testGuardarTipoCultivo() {
        // ARRANGE
        TipoCultivo cultivo = new TipoCultivo();
        cultivo.setDescripcion("Trigo");

        // ACT
        String vista = tipoCultivoController.guardarTipoCultivo(cultivo);

        // ASSERT
        assertEquals("redirect:/tiposcultivos/todos?success", vista);
        verify(tipoCultivoRepository, times(1)).save(cultivo);
    }

    // GET /tiposcultivos/editar/{uuid}
    @Test
    public void testEditarTipoCultivo() {
        // ARRANGE
        String uuid = "tc-123";

        TipoCultivo cultivo = new TipoCultivo();
        cultivo.setUuid(uuid);
        cultivo.setDescripcion("Maíz");

        when(tipoCultivoRepository.findByUuid(uuid))
                .thenReturn(Optional.of(cultivo));

        Model model = new ExtendedModelMap();

        // ACT
        String vista = tipoCultivoController.editarTipoCultivo(uuid, model);

        // ASSERT
        assertEquals("tipocultivo_form_editar", vista);
        assertEquals(cultivo, model.getAttribute("tipoCultivo"));
    }

    @Test
    public void testEditarTipoCultivo_NoEncontrado_LanzaExcepcion() {
        // ARRANGE
        String uuid = "no-existe";
        when(tipoCultivoRepository.findByUuid(uuid))
                .thenReturn(Optional.empty());

        Model model = new ExtendedModelMap();

        // ACT + ASSERT
        assertThrows(RuntimeException.class,
                () -> tipoCultivoController.editarTipoCultivo(uuid, model));
    }

    // POST /tiposcultivos/actualizar/{uuid}
    @Test
    public void testActualizarTipoCultivo() {
        // ARRANGE
        String uuid = "tc-123";

        TipoCultivo existente = new TipoCultivo();
        existente.setUuid(uuid);
        existente.setDescripcion("Viejo");

        when(tipoCultivoRepository.findByUuid(uuid))
                .thenReturn(Optional.of(existente));

        TipoCultivo actualizado = new TipoCultivo();
        actualizado.setDescripcion("Nuevo");

        // ACT
        String vista = tipoCultivoController.actualizar(uuid, actualizado);

        // ASSERT
        assertEquals("redirect:/tiposcultivos/todos?updated", vista);
        assertEquals("Nuevo", existente.getDescripcion());
        verify(tipoCultivoRepository).save(existente);
    }

    @Test
    public void testActualizarTipoCultivo_NoEncontrado_LanzaExcepcion() {
        // ARRANGE
        String uuid = "no-existe";
        TipoCultivo actualizado = new TipoCultivo();
        actualizado.setDescripcion("Algo");

        when(tipoCultivoRepository.findByUuid(uuid))
                .thenReturn(Optional.empty());

        // ACT + ASSERT
        assertThrows(RuntimeException.class,
                () -> tipoCultivoController.actualizar(uuid, actualizado));
    }

    // POST /tiposcultivos/eliminar/{uuid}    
    @Test
    public void testEliminarTipoCultivo() {
        // ARRANGE
        String uuid = "tc-123";

        TipoCultivo cultivo = new TipoCultivo();
        cultivo.setUuid(uuid);

        when(tipoCultivoRepository.findByUuid(uuid))
                .thenReturn(Optional.of(cultivo));

        // ACT
        String vista = tipoCultivoController.eliminar(uuid);

        // ASSERT
        assertEquals("redirect:/tiposcultivos/todos?deleted", vista);
        verify(tipoCultivoRepository).delete(cultivo);
    }

    @Test
    public void testEliminarTipoCultivo_NoEncontrado_LanzaExcepcion() {
        // ARRANGE
        String uuid = "no-existe";
        when(tipoCultivoRepository.findByUuid(uuid))
                .thenReturn(Optional.empty());

        // ACT + ASSERT
        assertThrows(RuntimeException.class,
                () -> tipoCultivoController.eliminar(uuid));
    }
}
