package com.example.web_seguro;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LogoutControllerTest {

    @Mock
    private HttpServletResponse response;

    private final LogoutController logoutController = new LogoutController();

    @AfterEach
    void limpiarContexto() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testLogout_LimpiaCookieYContexto() {
        // ARRANGE: simula que hay un usuario autenticado
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("user@test.com", null, List.of())
        );

        // ACT
        String vista = logoutController.logout(response);

        // ASSERT: vista correcta
        assertEquals("redirect:/login", vista);

        // Captura la cookie enviada
        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(response, times(1)).addCookie(cookieCaptor.capture());

        Cookie cookie = cookieCaptor.getValue();
        assertEquals("jwt_token", cookie.getName());
        assertNull(cookie.getValue());       // se borra
        assertEquals(0, cookie.getMaxAge()); // maxAge=0 => eliminar
        assertEquals("/", cookie.getPath());
        assertTrue(cookie.isHttpOnly());

        // Contexto de seguridad debe quedar vacío
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
