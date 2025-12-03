package com.example.web_seguro;

import com.example.web_seguro.model.Empresa;
import com.example.web_seguro.model.Maquinarias;
import com.example.web_seguro.model.TipoMaquinaria;
import com.example.web_seguro.service.EmpresaService;
import com.example.web_seguro.service.MaquinariasService;
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
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaquinariasControllerTest {

    @Mock
    private MaquinariasService maquinariasService;

    @Mock
    private TipoMaquinariaService tipoMaquinariaService;

    @Mock
    private EmpresaService empresaService;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private MaquinariasController controller;

    // MÉTODO @ModelAttribute cargarTipos() 
    @Test
    void testCargarTipos() {
        List<TipoMaquinaria> tipos = List.of(new TipoMaquinaria());
        when(tipoMaquinariaService.listaTipoMaquinarias()).thenReturn(tipos);

        List<TipoMaquinaria> result = controller.cargarTipos();

        assertEquals(tipos, result);
        verify(tipoMaquinariaService).listaTipoMaquinarias();
    }

    // GET /maquinarias 
    @Test
    void testVistaMaquinarias() {
        Model model = new ExtendedModelMap();

        List<TipoMaquinaria> tipos = List.of(new TipoMaquinaria());
        List<Maquinarias> lista = List.of(new Maquinarias());
        List<Empresa> empresas = List.of(new Empresa());

        when(tipoMaquinariaService.listaTipoMaquinarias()).thenReturn(tipos);
        when(maquinariasService.listaMaquinarias()).thenReturn(lista);
        when(empresaService.listaEmpresas()).thenReturn(empresas);

        String view = controller.vistaMaquinarias(model);

        assertEquals("maquinarias", view);
        assertEquals(lista, model.getAttribute("lista"));
        assertEquals(tipos, model.getAttribute("listaTipos"));
        assertEquals(empresas, model.getAttribute("listaEmpresas"));
        Object maqAttr = model.getAttribute("maq");
        assertEquals(Maquinarias.class, maqAttr.getClass());
    }

    // POST /guardarMaquinaria 
    // errores de validación (cubre el lambda getFieldErrors().forEach...)
    @Test
    void testGuardarMaquinaria_ErroresValidacion() {
        Maquinarias maq = new Maquinarias();
        Model model = new ExtendedModelMap();

        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldErrors())
                .thenReturn(List.of(new FieldError("maq", "campo", "mensaje")));

        List<Maquinarias> lista = List.of(new Maquinarias());
        when(maquinariasService.listaMaquinarias()).thenReturn(lista);

        String view = controller.guardarMaquinaria(maq, bindingResult, model, redirectAttributes);

        assertEquals("maquinarias", view);
        assertEquals(lista, model.getAttribute("lista"));
        verify(maquinariasService, never()).guardarMaquinaria(any());
        verify(redirectAttributes, never()).addFlashAttribute(eq("success"), any());
    }

    // ok
    @Test
    void testGuardarMaquinaria_OK() {
        Maquinarias maq = new Maquinarias();
        Model model = new ExtendedModelMap();

        when(bindingResult.hasErrors()).thenReturn(false);

        String view = controller.guardarMaquinaria(maq, bindingResult, model, redirectAttributes);

        assertEquals("redirect:/maquinarias#alerts", view);
        verify(maquinariasService).guardarMaquinaria(maq);
        verify(redirectAttributes).addFlashAttribute(eq("success"), contains("guardada"));
    }

    // DataIntegrityViolationException
    @Test
    void testGuardarMaquinaria_IntegridadViolada() {
        Maquinarias maq = new Maquinarias();
        Model model = new ExtendedModelMap();

        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new DataIntegrityViolationException("dup"))
                .when(maquinariasService).guardarMaquinaria(maq);

        String view = controller.guardarMaquinaria(maq, bindingResult, model, redirectAttributes);

        assertEquals("redirect:/maquinarias#alerts", view);
        verify(redirectAttributes)
                .addFlashAttribute(eq("error"), contains("No se pudo guardar"));
    }

    // Exception genérica (cubre catch (Exception ex) de guardar)
    @Test
    void testGuardarMaquinaria_ErrorGenerico() {
        Maquinarias maq = new Maquinarias();
        Model model = new ExtendedModelMap();

        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new RuntimeException("fallo"))
                .when(maquinariasService).guardarMaquinaria(maq);

        String view = controller.guardarMaquinaria(maq, bindingResult, model, redirectAttributes);

        assertEquals("redirect:/maquinarias#alerts", view);
        verify(redirectAttributes)
                .addFlashAttribute(eq("error"), contains("Error interno al guardar"));
    }

    // GET /editar/{uuid} 

    @Test
    void testEditarMaquinaria_OK() {
        String uuid = "uuid-123";
        Maquinarias maq = new Maquinarias();
        Model model = new ExtendedModelMap();
        List<Empresa> empresas = List.of(new Empresa());

        when(maquinariasService.buscaMaquinariaPorUuid(uuid)).thenReturn(Optional.of(maq));
        when(maquinariasService.listaMaquinarias()).thenReturn(List.of(maq));
        when(tipoMaquinariaService.listaTipoMaquinarias()).thenReturn(List.of(new TipoMaquinaria()));
        when(empresaService.listaEmpresas()).thenReturn(empresas);

        String view = controller.editarMaquinaria(uuid, model, redirectAttributes);

        assertEquals("maquinarias", view);
        assertEquals(maq, model.getAttribute("maq"));
        assertEquals(true, model.getAttribute("editMode"));
    }

    @Test
    void testEditarMaquinaria_NoExiste() {
        String uuid = "uuid-404";
        Model model = new ExtendedModelMap();

        when(maquinariasService.buscaMaquinariaPorUuid(uuid)).thenReturn(Optional.empty());

        String view = controller.editarMaquinaria(uuid, model, redirectAttributes);

        assertEquals("redirect:/maquinarias#alerts", view);
        verify(redirectAttributes)
                .addFlashAttribute(eq("error"), contains("no existe"));
    }

    // POST /actualizar/{uuid} 
    // errores de validación (ya cubre llamar a cargarTipos() y setear editMode)
    @Test
    void testActualizarMaquinaria_ErroresValidacion() {
        String uuid = "uuid-123";
        Maquinarias maq = new Maquinarias();
        Model model = new ExtendedModelMap();
        List<Empresa> empresas = List.of(new Empresa());

        when(bindingResult.hasErrors()).thenReturn(true);
        when(maquinariasService.listaMaquinarias()).thenReturn(List.of(maq));
        when(tipoMaquinariaService.listaTipoMaquinarias()).thenReturn(List.of(new TipoMaquinaria()));
        when(empresaService.listaEmpresas()).thenReturn(empresas);

        String view = controller.actualizarMaquinaria(uuid, maq, bindingResult, model, redirectAttributes);

        assertEquals("maquinarias", view);
        assertEquals(true, model.getAttribute("editMode"));
        verify(maquinariasService, never()).actualizarMaquinaria(anyString(), any());
    }

    // ok
    @Test
    void testActualizarMaquinaria_OK() {
        String uuid = "uuid-123";
        Maquinarias maq = new Maquinarias();
        Model model = new ExtendedModelMap();

        when(bindingResult.hasErrors()).thenReturn(false);

        String view = controller.actualizarMaquinaria(uuid, maq, bindingResult, model, redirectAttributes);

        assertEquals("redirect:/maquinarias#alerts", view);
        verify(maquinariasService).actualizarMaquinaria(uuid, maq);
        verify(redirectAttributes)
                .addFlashAttribute(eq("success"), contains("actualizada"));
    }

    // DataIntegrityViolationException
    @Test
    void testActualizarMaquinaria_IntegridadViolada() {
        String uuid = "uuid-123";
        Maquinarias maq = new Maquinarias();
        Model model = new ExtendedModelMap();

        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new DataIntegrityViolationException("dup"))
                .when(maquinariasService).actualizarMaquinaria(uuid, maq);

        String view = controller.actualizarMaquinaria(uuid, maq, bindingResult, model, redirectAttributes);

        assertEquals("redirect:/maquinarias#alerts", view);
        verify(redirectAttributes)
                .addFlashAttribute(eq("error"), contains("No se pudo actualizar"));
    }

    // RuntimeException (cubre catch (RuntimeException ex))
    @Test
    void testActualizarMaquinaria_RuntimeException() {
        String uuid = "uuid-123";
        Maquinarias maq = new Maquinarias();
        Model model = new ExtendedModelMap();

        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new RuntimeException("mensaje runtime"))
                .when(maquinariasService).actualizarMaquinaria(uuid, maq);

        String view = controller.actualizarMaquinaria(uuid, maq, bindingResult, model, redirectAttributes);

        assertEquals("redirect:/maquinarias#alerts", view);
        verify(redirectAttributes)
                .addFlashAttribute(eq("error"), eq("mensaje runtime"));
    }

    
    // GET /eliminar/{uuid} 
    @Test
    void testEliminarMaquinaria_OK() {
        String uuid = "uuid-123";

        String view = controller.eliminarMaquinaria(uuid, redirectAttributes);

        assertEquals("redirect:/maquinarias#alerts", view);
        verify(maquinariasService).eliminarMaquinaria(uuid);
        verify(redirectAttributes)
                .addFlashAttribute(eq("success"), contains("eliminada"));
    }

    @Test
    void testEliminarMaquinaria_IntegridadViolada() {
        String uuid = "uuid-123";

        doThrow(new DataIntegrityViolationException("FK"))
                .when(maquinariasService).eliminarMaquinaria(uuid);

        String view = controller.eliminarMaquinaria(uuid, redirectAttributes);

        assertEquals("redirect:/maquinarias#alerts", view);
        verify(redirectAttributes)
                .addFlashAttribute(eq("error"), contains("No se puede eliminar"));
    }

    // RuntimeException (cubre catch (RuntimeException ex))
    @Test
    void testEliminarMaquinaria_RuntimeException() {
        String uuid = "uuid-123";

        doThrow(new RuntimeException("mensaje runtime"))
                .when(maquinariasService).eliminarMaquinaria(uuid);

        String view = controller.eliminarMaquinaria(uuid, redirectAttributes);

        assertEquals("redirect:/maquinarias#alerts", view);
        verify(redirectAttributes)
                .addFlashAttribute(eq("error"), eq("mensaje runtime"));
    }

}
