package com.example.web_seguro;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import jakarta.servlet.RequestDispatcher;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CustomErrorControllerTest {
    
private final CustomErrorController controller = new CustomErrorController();

    private HttpServletRequest mockRequestWithStatus(int statusCode) {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE))
                .thenReturn(statusCode);
        return request;
    }

    @Test
    public void testHandleError_403() {
        HttpServletRequest request = mockRequestWithStatus(403);
        Model model = new ExtendedModelMap();

        String result = controller.handleError(request, model);

        assertEquals("error/403", result);
    }

    @Test
    public void testHandleError_404() {
        HttpServletRequest request = mockRequestWithStatus(404);
        Model model = new ExtendedModelMap();

        String result = controller.handleError(request, model);

        assertEquals("error/404", result);
    }

    @Test
    public void testHandleError_500() {
        HttpServletRequest request = mockRequestWithStatus(500);
        Model model = new ExtendedModelMap();

        String result = controller.handleError(request, model);

        assertEquals("error/500", result);
    }

    @Test
    public void testHandleError_OtroCodigo() {
        HttpServletRequest request = mockRequestWithStatus(418); 
        Model model = new ExtendedModelMap();

        String result = controller.handleError(request, model);

        assertEquals("error/generic", result);
    }

    @Test
    public void testHandleError_SinStatus() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE))
                .thenReturn(null);

        Model model = new ExtendedModelMap();

        String result = controller.handleError(request, model);

        assertEquals("error/generic", result);
    }
}
