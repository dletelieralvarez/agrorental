package com.example.web_seguro;

import jakarta.servlet.RequestDispatcher;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import static org.junit.jupiter.api.Assertions.*;

class WebSecurityConfigAccessDeniedTest {

    private final WebSecurityConfig config = new WebSecurityConfig();

    @Test
    void customAccessDeniedHandler_debeForwardearAErrorYSetear403() throws Exception {
        AccessDeniedHandler handler = config.customAccessDeniedHandler();

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.handle(request, response, new AccessDeniedException("sin permisos"));

        assertEquals(403, request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE));
        assertEquals("/error", response.getForwardedUrl());
    }
}