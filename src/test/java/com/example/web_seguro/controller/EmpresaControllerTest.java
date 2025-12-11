package com.example.web_seguro.controller;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import com.example.web_seguro.model.Empresa;
import com.example.web_seguro.model.TipoCultivo;
import com.example.web_seguro.model.Usuario;
import com.example.web_seguro.repository.EmpresaRepository;
import com.example.web_seguro.repository.TipoCultivoRepository;
import com.example.web_seguro.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
public class EmpresaControllerTest {

    @Mock
    private EmpresaRepository empresaRepository;

    @Mock
    private TipoCultivoRepository tipoCultivoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    // controlador que vamos a probar con los mocks inyectados
    @InjectMocks
    private EmpresaController empresaController;

    // GET EMPRESAS (usuario existe + empresas con y sin tipoCultivo)
    @Test
    public void testGetEmpresas() {

        // simula un usuario autenticado
        Authentication auth = new UsernamePasswordAuthenticationToken("user@test.com", null);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        // mock de usuario encontrado segun el mail
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("user@test.com");

        when(usuarioRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(usuario));

        // empresa 1 CON tipoCultivo
        Empresa e1 = new Empresa();
        TipoCultivo tipo = new TipoCultivo();
        tipo.setDescripcion("Trigo");
        e1.setTipoCultivo(tipo);

        // empresa 2 SIN tipoCultivo (null)
        Empresa e2 = new Empresa();

        List<Empresa> listaEmpresas = List.of(e1, e2);

        when(empresaRepository.findByUsuarioIdOrderByRazonSocialAsc(1L))
                .thenReturn(listaEmpresas);

        Model model = new ExtendedModelMap();

        // ACT
        String vista = empresaController.getEmpresas(null, model);

        // ASSERT
        assertEquals("mis_empresas", vista,
                "La vista devuelta debe ser 'mis_empresas'");
        assertEquals(listaEmpresas, model.getAttribute("empresas"),
                "El modelo debe contener la lista de empresas");
        assertEquals(1L, model.getAttribute("id"),
                "El modelo debe contener el id del usuario");
    }

    // GET EMPRESAS cuando NO se encuentra el usuario
    @Test
    public void testGetEmpresas_UsuarioNoEncontrado_LanzaExcepcion() {

        // simula un usuario autenticado
        Authentication auth = new UsernamePasswordAuthenticationToken("user@test.com", null);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        // mock: no se encuentra el usuario
        when(usuarioRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.empty());

        Model model = new ExtendedModelMap();

        // ACT + ASSERT: debe lanzar RuntimeException
        assertThrows(RuntimeException.class,
                () -> empresaController.getEmpresas(null, model),
                "Debe lanzar RuntimeException cuando el usuario no existe");
    }

    // TEST 2 GUARDAR EMPRESA
    @Test
    public void testGuardarEmpresa() {
        // Usuario autenticado
        Authentication auth = new UsernamePasswordAuthenticationToken("user@test.com", null);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("user@test.com");

        when(usuarioRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(usuario));

        // Tipo de cultivo
        TipoCultivo tipo = new TipoCultivo();
        tipo.setUuid("tc-uuid");
        when(tipoCultivoRepository.findByUuid("tc-uuid"))
                .thenReturn(Optional.of(tipo));

        // Empresa que se va a guardar
        Empresa empresa = new Empresa();
        empresa.setRazonSocial("Mi Empresa");

        // ACT
        String resultado = empresaController.guardarEmpresa(empresa, "tc-uuid");

        // ASSERT
        assertEquals("redirect:/empresas?success", resultado,
                "Debe redirigir con parámetro success");

        // Verifica que se llamó a save con una empresa
        verify(empresaRepository, times(1)).save(empresa);
        // Y que se asignó usuario y tipo
        assertEquals(usuario, empresa.getUsuario());
        assertEquals(tipo, empresa.getTipoCultivo());
    }

    @Test
    public void testMostrarFormulario() {
        // ARRANGE
        TipoCultivo t1 = new TipoCultivo();
        TipoCultivo t2 = new TipoCultivo();
        List<TipoCultivo> lista = List.of(t1, t2);

        when(tipoCultivoRepository.findAllByOrderByDescripcionAsc())
                .thenReturn(lista);

        Model model = new ExtendedModelMap();

        // ACT
        String vista = empresaController.mostrarFormulario(model);

        // ASSERT
        assertEquals("empresa_form", vista);
        assertNotNull(model.getAttribute("empresa"));
        assertEquals(lista, model.getAttribute("tiposCultivos"));
    }

    @Test
    public void testMostrarFormularioEditar() {
        // ARRANGE
        String uuid = "emp-123";

        Empresa empresa = new Empresa();
        empresa.setUuid(uuid);

        when(empresaRepository.findByUuid(uuid))
                .thenReturn(Optional.of(empresa));

        List<TipoCultivo> tipos = List.of(new TipoCultivo());
        when(tipoCultivoRepository.findAll()).thenReturn(tipos);

        Model model = new ExtendedModelMap();

        // ACT
        String vista = empresaController.mostrarFormularioEditar(uuid, model);

        // ASSERT
        assertEquals("empresa_form_editar", vista);
        assertEquals(empresa, model.getAttribute("empresa"));
        assertEquals(tipos, model.getAttribute("tiposCultivos"));
    }

    @Test
    public void testActualizarEmpresa() {
        // ARRANGE
        String uuid = "emp-123";

        Empresa existente = new Empresa();
        existente.setUuid(uuid);
        existente.setRazonSocial("Vieja");
        existente.setDireccion("Vieja dir");

        when(empresaRepository.findByUuid(uuid))
                .thenReturn(Optional.of(existente));

        TipoCultivo tipoNuevo = new TipoCultivo();
        tipoNuevo.setUuid("tc-uuid");
        when(tipoCultivoRepository.findByUuid("tc-uuid"))
                .thenReturn(Optional.of(tipoNuevo));

        // datos nuevos
        Empresa actualizada = new Empresa();
        actualizada.setRazonSocial("Nueva");
        actualizada.setDireccion("Nueva dir");
        actualizada.setTelefono("123");
        actualizada.setNota("nota nueva");

        // ACT
        String vista = empresaController.actualizarEmpresa(uuid, actualizada, "tc-uuid");

        // ASSERT
        assertEquals("redirect:/empresas?updated", vista);

        // se actualiza el objeto existente
        assertEquals("Nueva", existente.getRazonSocial());
        assertEquals("Nueva dir", existente.getDireccion());
        assertEquals("123", existente.getTelefono());
        assertEquals("nota nueva", existente.getNota());
        assertEquals(tipoNuevo, existente.getTipoCultivo());

        verify(empresaRepository).save(existente);
    }

    @Test
    public void testEliminarEmpresa_CuandoEsDelUsuario() {
        // ARRANGE: usuario autenticado
        Authentication auth =
                new UsernamePasswordAuthenticationToken("user@test.com", null);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("user@test.com");

        when(usuarioRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(usuario));

        Empresa empresa = new Empresa();
        empresa.setUuid("emp-123");
        empresa.setUsuario(usuario); // pertenece al mismo usuario

        when(empresaRepository.findByUuid("emp-123"))
                .thenReturn(Optional.of(empresa));

        // ACT
        String vista = empresaController.eliminar("emp-123");

        // ASSERT
        assertEquals("redirect:/empresas?deleted", vista);
        verify(empresaRepository).delete(empresa);
    }

    @Test
    public void testEliminarEmpresa_NoEsDelUsuario_LanzaExcepcion() {
        // ARRANGE: usuario autenticado
        Authentication auth =
                new UsernamePasswordAuthenticationToken("user@test.com", null);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("user@test.com");

        when(usuarioRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(usuario));

        // empresa pertenece a OTRO usuario
        Usuario otro = new Usuario();
        otro.setId(2L);

        Empresa empresa = new Empresa();
        empresa.setUuid("emp-123");
        empresa.setUsuario(otro);

        when(empresaRepository.findByUuid("emp-123"))
                .thenReturn(Optional.of(empresa));

        // ACT + ASSERT
        assertThrows(RuntimeException.class,
                () -> empresaController.eliminar("emp-123"));
    }
}
