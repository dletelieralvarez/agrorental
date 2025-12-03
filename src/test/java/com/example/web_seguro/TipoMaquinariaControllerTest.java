package com.example.web_seguro;

import com.example.web_seguro.model.TipoMaquinaria;
import com.example.web_seguro.service.TipoMaquinariaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TipoMaquinariaControllerTest {

    @Mock
    private TipoMaquinariaService tipoMaqService;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private TipoMaquinariaController controller;

    // GET /tipomaquinaria
    @Test
    void testVistaTipoMaq() {
        Model model = new ExtendedModelMap();
        List<TipoMaquinaria> lista = List.of(new TipoMaquinaria());

        when(tipoMaqService.listaTipoMaquinarias()).thenReturn(lista);

        String view = controller.vistaTipoMaq(model);

        assertEquals("tipomaquinaria", view);
        // se crea objeto vacío
        Object tipoAttr = model.getAttribute("tipo");
        assertEquals(TipoMaquinaria.class, tipoAttr.getClass());
        // se carga la lista
        assertEquals(lista, model.getAttribute("lista"));
        verify(tipoMaqService).listaTipoMaquinarias();
    }

    // POST /guardarTipoMaquinaria - errores de validación
    @Test
    void testGuardarTipoMaquinaria_ErroresValidacion() {
        TipoMaquinaria tipo = new TipoMaquinaria();
        Model model = new ExtendedModelMap();

        when(bindingResult.hasErrors()).thenReturn(true);
        when(tipoMaqService.listaTipoMaquinarias())
                .thenReturn(List.of(new TipoMaquinaria()));

        String view = controller.guardarTipoMaquinaria(
                tipo, bindingResult, model, redirectAttributes);

        assertEquals("tipomaquinaria", view);
        verify(tipoMaqService, never()).guardarTipoMaquinaria(any());
        verify(redirectAttributes, never())
                .addFlashAttribute(eq("success"), any());
    }

    // POST /guardarTipoMaquinaria - OK
    @Test
    void testGuardarTipoMaquinaria_OK() {
        TipoMaquinaria tipo = new TipoMaquinaria();
        Model model = new ExtendedModelMap();

        when(bindingResult.hasErrors()).thenReturn(false);

        String view = controller.guardarTipoMaquinaria(
                tipo, bindingResult, model, redirectAttributes);

        assertEquals("redirect:/tipomaquinaria", view);
        verify(tipoMaqService).guardarTipoMaquinaria(tipo);
        verify(redirectAttributes)
                .addFlashAttribute(eq("success"),
                        contains("guardada correctamente"));
    }

    // POST /guardarTipoMaquinaria - DataIntegrityViolationException
    @Test
    void testGuardarTipoMaquinaria_IntegridadViolada() {
        TipoMaquinaria tipo = new TipoMaquinaria();
        Model model = new ExtendedModelMap();

        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new DataIntegrityViolationException("dup"))
                .when(tipoMaqService).guardarTipoMaquinaria(tipo);

        String view = controller.guardarTipoMaquinaria(
                tipo, bindingResult, model, redirectAttributes);

        assertEquals("redirect:/tipomaquinaria", view);
        verify(redirectAttributes)
                .addFlashAttribute(eq("error"),
                        contains("No se pudo guardar"));
    }

    // POST /guardarTipoMaquinaria - Exception generica (cubre catch(Exception))
    @Test
    void testGuardarTipoMaquinaria_ErrorGenerico() {
        TipoMaquinaria tipo = new TipoMaquinaria();
        Model model = new ExtendedModelMap();

        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new RuntimeException("fallo"))
                .when(tipoMaqService).guardarTipoMaquinaria(tipo);

        String view = controller.guardarTipoMaquinaria(
                tipo, bindingResult, model, redirectAttributes);

        assertEquals("redirect:/tipomaquinaria", view);
        verify(redirectAttributes)
                .addFlashAttribute(eq("error"),
                        contains("Error interno al guardar"));
    }

    // GET /editar/{uuid} - existe
    @Test
    void testEditarTipoMaquinaria_OK() {
        String uuid = "uuid-123";
        TipoMaquinaria tipo = new TipoMaquinaria();
        Model model = new ExtendedModelMap();
        List<TipoMaquinaria> lista = List.of(tipo);

        when(tipoMaqService.buscarTipoMaquinariaPorUuid(uuid))
                .thenReturn(Optional.of(tipo));
        when(tipoMaqService.listaTipoMaquinarias())
                .thenReturn(lista);

        String view = controller.editarTipoMaquinaria(uuid, model, redirectAttributes);

        assertEquals("tipomaquinaria", view);
        assertEquals(tipo, model.getAttribute("tipo"));
        assertEquals(lista, model.getAttribute("lista"));
        assertEquals(true, model.getAttribute("editMode"));
    }

    // GET /editar/{uuid} - NO existe
    @Test
    void testEditarTipoMaquinaria_NoExiste() {
        String uuid = "uuid-404";
        Model model = new ExtendedModelMap();

        when(tipoMaqService.buscarTipoMaquinariaPorUuid(uuid))
                .thenReturn(Optional.empty());

        String view = controller.editarTipoMaquinaria(uuid, model, redirectAttributes);

        assertEquals("redirect:/tipomaquinaria", view);
        verify(redirectAttributes)
                .addFlashAttribute(eq("error"),
                        contains("no existe"));
    }

    // POST /actualizar/{uuid} - errores validación
    @Test
    void testActualizarTipoMaquinaria_ErroresValidacion() {
        String uuid = "uuid-123";
        TipoMaquinaria tipo = new TipoMaquinaria();
        Model model = new ExtendedModelMap();

        when(bindingResult.hasErrors()).thenReturn(true);
        when(tipoMaqService.listaTipoMaquinarias())
                .thenReturn(List.of(tipo));

        String view = controller.actualizarTipoMaquinaria(
                uuid, tipo, bindingResult, model, redirectAttributes);

        assertEquals("tipomaquinaria", view);
        assertEquals(true, model.getAttribute("editMode"));
        verify(tipoMaqService, never())
                .actualizaTipoMaquinaria(anyString(), any());
    }

    // POST /actualizar/{uuid} - OK
    @Test
    void testActualizarTipoMaquinaria_OK() {
        String uuid = "uuid-123";
        TipoMaquinaria tipo = new TipoMaquinaria();
        Model model = new ExtendedModelMap();

        when(bindingResult.hasErrors()).thenReturn(false);

        String view = controller.actualizarTipoMaquinaria(
                uuid, tipo, bindingResult, model, redirectAttributes);

        assertEquals("redirect:/tipomaquinaria", view);
        verify(tipoMaqService).actualizaTipoMaquinaria(uuid, tipo);
        verify(redirectAttributes)
                .addFlashAttribute(eq("success"),
                        contains("actualizado correctamente"));
    }

    // POST /actualizar/{uuid} - DataIntegrityViolationException
    @Test
    void testActualizarTipoMaquinaria_IntegridadViolada() {
        String uuid = "uuid-123";
        TipoMaquinaria tipo = new TipoMaquinaria();
        Model model = new ExtendedModelMap();

        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new DataIntegrityViolationException("dup"))
                .when(tipoMaqService).actualizaTipoMaquinaria(uuid, tipo);

        String view = controller.actualizarTipoMaquinaria(
                uuid, tipo, bindingResult, model, redirectAttributes);

        assertEquals("redirect:/tipomaquinaria", view);
        verify(redirectAttributes)
                .addFlashAttribute(eq("error"),
                        contains("No se pudo actualizar"));
    }

    // POST /actualizar/{uuid} - RuntimeException (cubre catch(RuntimeException))
    @Test
    void testActualizarTipoMaquinaria_RuntimeException() {
        String uuid = "uuid-123";
        TipoMaquinaria tipo = new TipoMaquinaria();
        Model model = new ExtendedModelMap();

        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new RuntimeException("mensaje runtime"))
                .when(tipoMaqService).actualizaTipoMaquinaria(uuid, tipo);

        String view = controller.actualizarTipoMaquinaria(
                uuid, tipo, bindingResult, model, redirectAttributes);

        assertEquals("redirect:/tipomaquinaria", view);
        verify(redirectAttributes)
                .addFlashAttribute(eq("error"), eq("mensaje runtime"));
    }

    // GET /eliminar/{uuid} - OK
    @Test
    void testEliminarTipoMaquinaria_OK() {
        String uuid = "uuid-123";

        String view = controller.eliminarTipoMaquinaria(uuid, redirectAttributes);

        assertEquals("redirect:/tipomaquinaria", view);
        verify(tipoMaqService).eliminarTipoMaquinaria(uuid);
        verify(redirectAttributes)
                .addFlashAttribute(eq("success"),
                        contains("eliminado correctamente"));
    }

    // GET /eliminar/{uuid} - DataIntegrityViolationException
    @Test
    void testEliminarTipoMaquinaria_IntegridadViolada() {
        String uuid = "uuid-123";

        doThrow(new DataIntegrityViolationException("FK"))
                .when(tipoMaqService).eliminarTipoMaquinaria(uuid);

        String view = controller.eliminarTipoMaquinaria(uuid, redirectAttributes);

        assertEquals("redirect:/tipomaquinaria", view);
        verify(redirectAttributes)
                .addFlashAttribute(eq("error"),
                        contains("No se puede eliminar"));
    }

    // GET /eliminar/{uuid} - RuntimeException
    @Test
    void testEliminarTipoMaquinaria_RuntimeException() {
        String uuid = "uuid-123";

        doThrow(new RuntimeException("mensaje runtime"))
                .when(tipoMaqService).eliminarTipoMaquinaria(uuid);

        String view = controller.eliminarTipoMaquinaria(uuid, redirectAttributes);

        assertEquals("redirect:/tipomaquinaria", view);
        verify(redirectAttributes)
                .addFlashAttribute(eq("error"), eq("mensaje runtime"));
    }
}
