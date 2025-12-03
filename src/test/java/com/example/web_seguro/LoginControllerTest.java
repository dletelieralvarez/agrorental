package com.example.web_seguro;

import com.example.web_seguro.service.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;

import jakarta.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoginControllerTest {

    @Mock
    private JWTAuthenticationConfig jwtAuthenticationConfig;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Model model;

    @InjectMocks
    private LoginController loginController;

    // GET /account 
    @Test
    public void testShowLoginPage() {
        String vista = loginController.showLoginPage();
        assertEquals("account", vista);
    }

    // GET /login (redirect)
    @Test
    public void testRedirectToAccount() {
        String vista = loginController.redirectToAccount();
        assertEquals("redirect:/account", vista);
    }

    // POST /login: éxito 
    @Test
    public void testLogin_CredencialesCorrectas() {
        // ARRANGE
        String username = "user@test.com";
        String plainPass = "1234";
        String encodedPass = "{bcrypt}asdfgh";

        UserDetails userDetails = User.withUsername(username)
                .password(encodedPass)
                .authorities("ROLE_USER")
                .build();

        when(userDetailsService.loadUserByUsername(username))
                .thenReturn(userDetails);
        when(passwordEncoder.matches(plainPass, encodedPass))
                .thenReturn(true);

        // el config devuelve un token con "Bearer "
        when(jwtAuthenticationConfig.getJWTToken(userDetails))
                .thenReturn("Bearer 12345678901234567890.xyz");

        // ACT
        String vista = loginController.login(username, plainPass, response, model);

        // ASSERT
        assertEquals("redirect:/", vista);

        // se genera la cookie y se envía en el header
        verify(response).addHeader(eq(HttpHeaders.SET_COOKIE),
                argThat((ArgumentMatcher<String>) value ->
                        value.contains("jwt_token=") &&
                        value.contains("12345678901234567890.xyz") &&   // sin el "Bearer "
                        !value.contains("Bearer ")
                ));

        // no debe colocar mensaje de error
        verify(model, never()).addAttribute(eq("error"), any());
    }

    // POST /login: contraseña incorrecta 
    @Test
    public void testLogin_ContrasenaIncorrecta() {
        String username = "user@test.com";
        String plainPass = "incorrecta";
        String encodedPass = "{bcrypt}asdfgh";

        UserDetails userDetails = User.withUsername(username)
                .password(encodedPass)
                .authorities("ROLE_USER")
                .build();

        when(userDetailsService.loadUserByUsername(username))
                .thenReturn(userDetails);
        when(passwordEncoder.matches(plainPass, encodedPass))
                .thenReturn(false);

        String vista = loginController.login(username, plainPass, response, model);

        assertEquals("account", vista);
        verify(model).addAttribute(eq("error"), anyString());
        // no se debe generar token ni cookie
        verify(jwtAuthenticationConfig, never()).getJWTToken(any());
        verify(response, never()).addHeader(eq(HttpHeaders.SET_COOKIE), anyString());
    }

    // POST /login: excepción (usuario no encontrado, etc.) 
    @Test
    public void testLogin_ExcepcionEnServicio_MuestraError() {
        String username = "user@test.com";
        String plainPass = "1234";

        when(userDetailsService.loadUserByUsername(username))
                .thenThrow(new RuntimeException("Usuario no encontrado"));

        String vista = loginController.login(username, plainPass, response, model);

        assertEquals("account", vista);
        verify(model).addAttribute(eq("error"), contains("Error en autenticación"));
        verify(response, never()).addHeader(eq(HttpHeaders.SET_COOKIE), anyString());
    }

    // TESTS de sanitize()
    @Test
    public void testSanitize_NullDevuelveVacio() {
        String result = loginController.sanitize(null);
        assertEquals("", result);
    }

    @Test
    public void testSanitize_ReemplazaSaltosYTabsPorEspacio() {             
        String original = "hola\nmundo\ty\rchau";
        String result = loginController.sanitize(original);
        assertEquals("hola_mundo_y_chau", result);
    }
}
