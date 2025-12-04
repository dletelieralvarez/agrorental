package com.example.web_seguro;

import com.example.web_seguro.model.Usuario;
import com.example.web_seguro.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class RegistroControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private RegistroController registroController;

    // GET /signup 
    @Test
    public void testMostrarFormularioRegistro() {
        Model model = new ExtendedModelMap();

        String view = registroController.mostrarFormularioRegistro(model);

        assertEquals("signup", view);
        Object usuarioAttr = model.getAttribute("usuario");
        // hay un objeto usuario en el modelo
        assertEquals(Usuario.class, usuarioAttr.getClass());
    }

    // POST /signup: errores de validación 
    @Test
    public void testRegistrarUsuario_ErroresValidacion() {
        Usuario usuario = new Usuario();
        Model model = new ExtendedModelMap();

        when(bindingResult.hasErrors()).thenReturn(true);
       
        when(bindingResult.getFieldErrors())
                .thenReturn(List.of(new FieldError("usuario", "email", "Email invalido")));

        String view = registroController.registrarUsuario(usuario, bindingResult, model);

        assertEquals("signup", view);
        verify(usuarioService, never()).registrarUsuario(any());
    }
    

    // POST /signup: registro OK 
    @Test
    public void testRegistrarUsuario_OK() {
        Usuario usuario = new Usuario();
        Model model = new ExtendedModelMap();

        when(bindingResult.hasErrors()).thenReturn(false);

        String view = registroController.registrarUsuario(usuario, bindingResult, model);

        assertEquals("redirect:/login?registroExitoso", view);
        verify(usuarioService, times(1)).registrarUsuario(usuario);
    }

    // POST /signup: servicio lanza IllegalArgumentException 
    @Test
    public void testRegistrarUsuario_IllegalArgumentException() {
        Usuario usuario = new Usuario();
        Model model = new ExtendedModelMap();

        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new IllegalArgumentException("Email ya registrado"))
                .when(usuarioService).registrarUsuario(usuario);

        String view = registroController.registrarUsuario(usuario, bindingResult, model);

        assertEquals("signup", view);
        assertEquals("Email ya registrado", model.getAttribute("error"));
    }
}
