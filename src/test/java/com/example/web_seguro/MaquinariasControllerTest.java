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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MaquinariasControllerTest {

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

    // GET /maquinarias 
    @Test
    public void testVistaMaquinarias() {
        Model model = new ExtendedModelMap();

        List<TipoMaquinaria> tipos = List.of(new TipoMaquinaria());
        List<Maquinarias> lista = List.of(new Maquinarias());
        //List<Object> empresas = List.of(new Object());
        List<Empresa> empresas = List.of(new Empresa());

        when(tipoMaquinariaService.listaTipoMaquinarias()).thenReturn(tipos);
        when(maquinariasService.listaMaquinarias()).thenReturn(lista);
        when(empresaService.listaEmpresas()).thenReturn(empresas);

        String view = controller.vistaMaquinarias(model);

        assertEquals("maquinarias", view);
        assertEquals(lista, model.getAttribute("lista"));
        assertEquals(tipos, model.getAttribute("listaTipos"));
        assertEquals(empresas, model.getAttribute("listaEmpresas"));
        // "maq" debe existir
        assertEquals(Maquinarias.class, model.getAttribute("maq").getClass());
    }

    // POST /guardarMaquinaria: errores de validación 
    @Test
    public void testGuardarMaquinaria_ErroresValidacion() {
        Maquinarias maq = new Maquinarias();
        Model model = new ExtendedModelMap();

        when(bindingResult.hasErrors()).thenReturn(true);
        List<Maquinarias> lista = List.of(new Maquinarias());
        when(maquinariasService.listaMaquinarias()).thenReturn(lista);

        String view = controller.guardarMaquinaria(maq, bindingResult, model, redirectAttributes);

        assertEquals("maquinarias", view);
        assertEquals(lista, model.getAttribute("lista"));
        verify(maquinariasService, never()).guardarMaquinaria(any());
        verify(redirectAttributes, never()).addFlashAttribute(eq("success"), any());
    }

    // POST /guardarMaquinaria: OK
    @Test
    public void testGuardarMaquinaria_OK() {
        Maquinarias maq = new Maquinarias();
        Model model = new ExtendedModelMap();

        when(bindingResult.hasErrors()).thenReturn(false);

        String view = controller.guardarMaquinaria(maq, bindingResult, model, redirectAttributes);

        assertEquals("redirect:/maquinarias#alerts", view);
        verify(maquinariasService, times(1)).guardarMaquinaria(maq);
        verify(redirectAttributes).addFlashAttribute(eq("success"), anyString());
    }

    // POST /guardarMaquinaria: DataIntegrityViolation 
    @Test
    public void testGuardarMaquinaria_IntegridadViolada() {
        Maquinarias maq = new Maquinarias();
        Model model = new ExtendedModelMap();

        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new DataIntegrityViolationException("dup"))
                .when(maquinariasService).guardarMaquinaria(maq);

        String view = controller.guardarMaquinaria(maq, bindingResult, model, redirectAttributes);

        assertEquals("redirect:/maquinarias#alerts", view);
        verify(redirectAttributes).addFlashAttribute(eq("error"), contains("No se pudo guardar"));
    }

    // GET /editar/{uuid}: maquinaria existe 
    @Test
    public void testEditarMaquinaria_OK() {
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

    // GET /editar/{uuid}: maquinaria NO existe 
    @Test
    public void testEditarMaquinaria_NoExiste() {
        String uuid = "uuid-404";
        Model model = new ExtendedModelMap();

        when(maquinariasService.buscaMaquinariaPorUuid(uuid)).thenReturn(Optional.empty());

        String view = controller.editarMaquinaria(uuid, model, redirectAttributes);

        assertEquals("redirect:/maquinarias#alerts", view);
        verify(redirectAttributes).addFlashAttribute(eq("error"), contains("Maquinaria no existe"));
    }

    // POST /actualizar/{uuid}: errores de validación 
    @Test
    public void testActualizarMaquinaria_ErroresValidacion() {
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

    // POST /actualizar/{uuid}: OK 
    @Test
    public void testActualizarMaquinaria_OK() {
        String uuid = "uuid-123";
        Maquinarias maq = new Maquinarias();
        Model model = new ExtendedModelMap();

        when(bindingResult.hasErrors()).thenReturn(false);

        String view = controller.actualizarMaquinaria(uuid, maq, bindingResult, model, redirectAttributes);

        assertEquals("redirect:/maquinarias#alerts", view);
        verify(maquinariasService).actualizarMaquinaria(uuid, maq);
        verify(redirectAttributes).addFlashAttribute(eq("success"), contains("actualizada"));
    }

    // GET /eliminar/{uuid}: OK 
    @Test
    public void testEliminarMaquinaria_OK() {
        String uuid = "uuid-123";

        String view = controller.eliminarMaquinaria(uuid, redirectAttributes);

        assertEquals("redirect:/maquinarias#alerts", view);
        verify(maquinariasService).eliminarMaquinaria(uuid);
        verify(redirectAttributes).addFlashAttribute(eq("success"), contains("eliminada"));
    }

    // GET /eliminar/{uuid}: DataIntegrityViolation 
    @Test
    public void testEliminarMaquinaria_IntegridadViolada() {
        String uuid = "uuid-123";

        doThrow(new DataIntegrityViolationException("FK"))
                .when(maquinariasService).eliminarMaquinaria(uuid);

        String view = controller.eliminarMaquinaria(uuid, redirectAttributes);

        assertEquals("redirect:/maquinarias#alerts", view);
        verify(redirectAttributes).addFlashAttribute(eq("error"), contains("No se puede eliminar"));
    }
}
