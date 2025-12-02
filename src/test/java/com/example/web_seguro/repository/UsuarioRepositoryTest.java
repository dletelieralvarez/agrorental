package com.example.web_seguro.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.web_seguro.model.Usuario;
import com.example.web_seguro.service.UsuarioService;

import com.example.web_seguro.model.Usuario;
import com.example.web_seguro.repository.UsuarioRepository;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


/**
 * Tests para UsuarioService
 * Prueba las funcionalidades de registro, encriptación de contraseñas y validaciones
 */
public class UsuarioRepositoryTest {
    @Mock
    private UsuarioRepository usuarioRepository;

   @InjectMocks
    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    //  guardarUsuario()

    @Test
    @DisplayName("Guardar usuario - debe encriptar la contraseña antes de guardar")
    void guardarUsuario_DebeEncriptarPassword() {
        // PREPARAR
        Usuario usuario = new Usuario();
        usuario.setEmail("test@test.com");
        usuario.setPassword("password123"); // Contraseña en texto plano
        usuario.setNombres("Test User");

        // Simulamos que el save devuelve el usuario
        when(usuarioRepository.save(any(Usuario.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // EJECUTAR
        Usuario resultado = usuarioService.guardarUsuario(usuario);

        // VERIFICAR
        assertNotNull(resultado);
        assertNotNull(resultado.getPassword(), "La contraseña no debe ser null");
        assertNotEquals("password123", resultado.getPassword(), 
            "La contraseña debe estar encriptada (no debe ser igual a la original)");
        assertTrue(resultado.getPassword().startsWith("$2a$") || 
                   resultado.getPassword().startsWith("$2b$"), 
            "La contraseña debe tener formato BCrypt");
        
        // Verificamos que se llamó al save
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    @DisplayName("Guardar usuario - la contraseña encriptada debe ser válida")
    void guardarUsuario_PasswordEncriptadaDebeSerValida() {
        // PREPARAR
        String passwordOriginal = "miPassword123";
        Usuario usuario = new Usuario();
        usuario.setEmail("test@test.com");
        usuario.setPassword(passwordOriginal);

        when(usuarioRepository.save(any(Usuario.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // EJECUTAR
        Usuario resultado = usuarioService.guardarUsuario(usuario);

        // VERIFICAR - Comprobamos que la contraseña encriptada sea verificable
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        assertTrue(encoder.matches(passwordOriginal, resultado.getPassword()),
            "La contraseña encriptada debe poder verificarse con BCrypt");
        
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    @DisplayName("Guardar usuario - debe mantener los demás datos del usuario")
    void guardarUsuario_DebeMantenerDatosDelUsuario() {
        // PREPARAR
        Usuario usuario = new Usuario();
        usuario.setEmail("juan@test.com");
        usuario.setPassword("password123");
        usuario.setNombres("Juan Perez");
        usuario.setRol("ADMIN");

        when(usuarioRepository.save(any(Usuario.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // EJECUTAR
        Usuario resultado = usuarioService.guardarUsuario(usuario);

        // VERIFICAR que los demás campos no cambiaron
        assertEquals("juan@test.com", resultado.getEmail());
        assertEquals("Juan Perez", resultado.getNombres());
        assertEquals("ADMIN", resultado.getRol());
        
        verify(usuarioRepository, times(1)).save(usuario);
    }

    //  verificarPassword() 

    @Test
    @DisplayName("Verificar password CORRECTA - debe retornar true")
    void verificarPassword_Correcta_DebeRetornarTrue() {
        // PREPARAR - Encriptamos una contraseña primero
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String passwordPlana = "miPasswordSecretaDuocUc";
        String passwordEncriptada = encoder.encode(passwordPlana);

        // EJECUTAR
        boolean resultado = usuarioService.verificarPassword(passwordPlana, passwordEncriptada);

        // VERIFICAR
        assertTrue(resultado, "Debe retornar true cuando la contraseña es correcta");
    }

    @Test
    @DisplayName("Verificar password INCORRECTA - debe retornar false")
    void verificarPassword_Incorrecta_DebeRetornarFalse() {
        // PREPARAR
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String passwordCorrecta = "passwordCorrecta123";
        String passwordEncriptada = encoder.encode(passwordCorrecta);
        String passwordIncorrecta = "passwordIncorrecta456";

        // EJECUTAR
        boolean resultado = usuarioService.verificarPassword(passwordIncorrecta, passwordEncriptada);

        // VERIFICAR
        assertFalse(resultado, "Debe retornar false cuando la contraseña es incorrecta");
    }

    @Test
    @DisplayName("Verificar password - diferentes contraseñas generan diferentes hashes")
    void verificarPassword_DiferentesPasswordsGeneranDiferentesHashes() {
        // PREPARAR
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password1 = "password123";
        String password2 = "password456";
        
        String hash1 = encoder.encode(password1);
        String hash2 = encoder.encode(password2);

        // VERIFICAR
        assertTrue(usuarioService.verificarPassword(password1, hash1));
        assertFalse(usuarioService.verificarPassword(password1, hash2), 
            "Password1 no debe coincidir con hash de password2");
        
        assertTrue(usuarioService.verificarPassword(password2, hash2));
        assertFalse(usuarioService.verificarPassword(password2, hash1),
            "Password2 no debe coincidir con hash de password1");
    }

    @Test
    @DisplayName("Verificar password vacía - debe retornar false")
    void verificarPassword_PasswordVacia_DebeRetornarFalse() {
        // PREPARAR
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String passwordEncriptada = encoder.encode("password123");

        // EJECUTAR
        boolean resultado = usuarioService.verificarPassword("", passwordEncriptada);

        // VERIFICAR
        assertFalse(resultado, "Contraseña vacía no debe ser válida");
    }

    //  registrarUsuario() 

    @Test
    @DisplayName("Registrar usuario NUEVO con todos los datos - debe registrar correctamente")
    void registrarUsuario_Nuevo_ConTodosDatos_DebeRegistrarCorrectamente() {
        // PREPARAR
        Usuario usuario = new Usuario();
        usuario.setEmail("nuevo@test.com");
        usuario.setPassword("password123");
        usuario.setNombres("Usuario Nuevo");
        usuario.setUuid("uuid-proporcionado-123");
        usuario.setRol("ADMIN");

        // Simulamos que NO existe un usuario con ese email
        when(usuarioRepository.findByEmail("nuevo@test.com"))
            .thenReturn(Optional.empty());

        when(usuarioRepository.save(any(Usuario.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // EJECUTAR
        Usuario resultado = usuarioService.registrarUsuario(usuario);

        // VERIFICAR
        assertNotNull(resultado);
        assertEquals("nuevo@test.com", resultado.getEmail());
        assertEquals("uuid-proporcionado-123", resultado.getUuid(), 
            "Debe mantener el UUID proporcionado");
        assertEquals("ADMIN", resultado.getRol(), 
            "Debe mantener el rol proporcionado");
        assertNotEquals("password123", resultado.getPassword(), 
            "La contraseña debe estar encriptada");
        
        verify(usuarioRepository, times(1)).findByEmail("nuevo@test.com");
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    @DisplayName("Registrar usuario SIN uuid - debe generar UUID automáticamente")
    void registrarUsuario_SinUuid_DebeGenerarUuidAutomaticamente() {
        // PREPARAR
        Usuario usuario = new Usuario();
        usuario.setEmail("nuevo@test.com");
        usuario.setPassword("password123");
        usuario.setNombres("Usuario Nuevo");
        usuario.setUuid(null); // Sin UUID
        usuario.setRol("USER");

        when(usuarioRepository.findByEmail("nuevo@test.com"))
            .thenReturn(Optional.empty());

        when(usuarioRepository.save(any(Usuario.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // EJECUTAR
        Usuario resultado = usuarioService.registrarUsuario(usuario);

        // VERIFICAR
        assertNotNull(resultado.getUuid(), "Debe generar un UUID");
        assertFalse(resultado.getUuid().isBlank(), "El UUID no debe estar vacío");
        
        verify(usuarioRepository, times(1)).findByEmail("nuevo@test.com");
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    @DisplayName("Registrar usuario con UUID vacio - debe generar UUID nuevo")
    void registrarUsuario_ConUuidVacio_DebeGenerarUuidNuevo() {
        // PREPARAR
        Usuario usuario = new Usuario();
        usuario.setEmail("nuevo@test.com");
        usuario.setPassword("password123");
        usuario.setNombres("Usuario Nuevo");
        usuario.setUuid(""); // UUID vacío
        usuario.setRol("USER");

        when(usuarioRepository.findByEmail("nuevo@test.com"))
            .thenReturn(Optional.empty());

        when(usuarioRepository.save(any(Usuario.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // EJECUTAR
        Usuario resultado = usuarioService.registrarUsuario(usuario);

        // VERIFICAR
        assertNotNull(resultado.getUuid());
        assertFalse(resultado.getUuid().isBlank(), "Debe generar un UUID nuevo");
        
        verify(usuarioRepository, times(1)).findByEmail("nuevo@test.com");
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    @DisplayName("Registrar usuario sin rol - debe asignar rol USER por defecto")
    void registrarUsuario_SinRol_DebeAsignarRolUserPorDefecto() {
        // PREPARAR
        Usuario usuario = new Usuario();
        usuario.setEmail("nuevo@test.com");
        usuario.setPassword("password123");
        usuario.setNombres("Usuario Nuevo");
        usuario.setRol(null); // Sin rol

        when(usuarioRepository.findByEmail("nuevo@test.com"))
            .thenReturn(Optional.empty());

        when(usuarioRepository.save(any(Usuario.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // EJECUTAR
        Usuario resultado = usuarioService.registrarUsuario(usuario);

        // VERIFICAR
        assertEquals("USER", resultado.getRol(), 
            "Debe asignar el rol USER por defecto");
        
        verify(usuarioRepository, times(1)).findByEmail("nuevo@test.com");
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    @DisplayName("Registrar usuario con rol VACÍO - debe asignar rol USER por defecto")
    void registrarUsuario_ConRolVacio_DebeAsignarRolUserPorDefecto() {
        // PREPARAR
        Usuario usuario = new Usuario();
        usuario.setEmail("nuevo@test.com");
        usuario.setPassword("password123");
        usuario.setNombres("Usuario Nuevo");
        usuario.setRol(""); // Rol vacío

        when(usuarioRepository.findByEmail("nuevo@test.com"))
            .thenReturn(Optional.empty());

        when(usuarioRepository.save(any(Usuario.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // EJECUTAR
        Usuario resultado = usuarioService.registrarUsuario(usuario);

        // VERIFICAR
        assertEquals("USER", resultado.getRol(), 
            "Debe asignar el rol USER cuando está vacío");
        
        verify(usuarioRepository, times(1)).findByEmail("nuevo@test.com");
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    @DisplayName("Registrar usuario - debe encriptar la contraseña con BCrypt")
    void registrarUsuario_DebeEncriptarPasswordConBCrypt() {
        // PREPARAR
        String passwordOriginal = "passwordSegura123";
        Usuario usuario = new Usuario();
        usuario.setEmail("nuevo@test.com");
        usuario.setPassword(passwordOriginal);
        usuario.setNombres("Usuario Nuevo");

        when(usuarioRepository.findByEmail("nuevo@test.com"))
            .thenReturn(Optional.empty());

        when(usuarioRepository.save(any(Usuario.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // EJECUTAR
        Usuario resultado = usuarioService.registrarUsuario(usuario);

        // VERIFICAR que la contraseña fue encriptada
        assertNotNull(resultado.getPassword());
        assertNotEquals(passwordOriginal, resultado.getPassword(), 
            "La contraseña debe estar encriptada");
        assertTrue(resultado.getPassword().startsWith("$2a$") || 
                   resultado.getPassword().startsWith("$2b$"),
            "Debe tener formato BCrypt");
        
        // Verificamos que la contraseña encriptada sea válida
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        assertTrue(encoder.matches(passwordOriginal, resultado.getPassword()),
            "La contraseña encriptada debe poder verificarse");
        
        verify(usuarioRepository, times(1)).findByEmail("nuevo@test.com");
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    @DisplayName("Registrar usuario con email existnete debe lanzar IllegalArgumentException")
    void registrarUsuario_EmailExistente_DebeLanzarExcepcion() {
        // PREPARAR
        String emailExistente = "existente@test.com";
        
        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setId(1L);
        usuarioExistente.setEmail(emailExistente);
        usuarioExistente.setPassword("oldPassword");

        Usuario usuarioNuevo = new Usuario();
        usuarioNuevo.setEmail(emailExistente); // Mismo email
        usuarioNuevo.setPassword("newPassword");
        usuarioNuevo.setNombres("Nuevo Usuario");

        // Simulamos que si existe un usuario con ese email
        when(usuarioRepository.findByEmail(emailExistente))
            .thenReturn(Optional.of(usuarioExistente));

        // EJECUTAR Y VERIFICAR
        IllegalArgumentException excepcion = assertThrows(
            IllegalArgumentException.class,
            () -> usuarioService.registrarUsuario(usuarioNuevo),
            "Debe lanzar IllegalArgumentException cuando el email ya existe"
        );

        // Verificamos el mensaje de error
        assertEquals("El correo electrónico ya está registrado.", 
            excepcion.getMessage());
        
        // Verificamos que se buscó el email
        verify(usuarioRepository, times(1)).findByEmail(emailExistente);
        
        // Verificamos que no se intentó guardar nada
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Registrar usuario completo - debe tener todos los campos correctos")
    void registrarUsuario_Completo_DebeTenerTodosLosCamposCorrectos() {
        // PREPARAR
        Usuario usuario = new Usuario();
        usuario.setEmail("completo@test.com");
        usuario.setPassword("password123");
        usuario.setNombres("Usuario Completo");
        usuario.setPrimerApellido("Apellido Test");
                usuario.setSegundoApellido("Apellido Test");

        when(usuarioRepository.findByEmail("completo@test.com"))
            .thenReturn(Optional.empty());

        when(usuarioRepository.save(any(Usuario.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // EJECUTAR
        Usuario resultado = usuarioService.registrarUsuario(usuario);

        // VERIFICAR todos los campos
        assertNotNull(resultado);
        assertEquals("completo@test.com", resultado.getEmail());
        assertEquals("Usuario Completo", resultado.getNombres());
        assertEquals("Apellido Test", resultado.getPrimerApellido());
                assertEquals("Apellido Test", resultado.getSegundoApellido());

        assertNotNull(resultado.getUuid(), "UUID debe estar generado");
        assertEquals("USER", resultado.getRol(), "Rol por defecto debe ser USER");
        assertNotEquals("password123", resultado.getPassword(), 
            "Password debe estar encriptada");
        
        verify(usuarioRepository, times(1)).findByEmail("completo@test.com");
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    @DisplayName("Registrar múltiples usuarios cada uno debe tener hash diferente")
    void registrarUsuario_MultiplesUsuarios_DebeTenerHashesDiferentes() {
        // PREPARAR - Dos usuarios con la misma contraseña
        String mismaPassword = "password123";
        
        Usuario usuario1 = new Usuario();
        usuario1.setEmail("usuario1@test.com");
        usuario1.setPassword(mismaPassword);

        Usuario usuario2 = new Usuario();
        usuario2.setEmail("usuario2@test.com");
        usuario2.setPassword(mismaPassword);

        when(usuarioRepository.findByEmail(anyString()))
            .thenReturn(Optional.empty());

        when(usuarioRepository.save(any(Usuario.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // EJECUTAR
        Usuario resultado1 = usuarioService.registrarUsuario(usuario1);
        Usuario resultado2 = usuarioService.registrarUsuario(usuario2);

        // VERIFICAR que aunque tengan la misma contraseña, 
        // los hashes son diferentes (característica de BCrypt con salt)
        assertNotEquals(resultado1.getPassword(), resultado2.getPassword(),
            "BCrypt debe generar hashes diferentes para la misma contraseña (debido al salt)");
        
        // Pero ambos deben poder verificarse con la contraseña original
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        assertTrue(encoder.matches(mismaPassword, resultado1.getPassword()));
        assertTrue(encoder.matches(mismaPassword, resultado2.getPassword()));
        
        verify(usuarioRepository, times(2)).findByEmail(anyString());
        verify(usuarioRepository, times(2)).save(any(Usuario.class));
    }
}
