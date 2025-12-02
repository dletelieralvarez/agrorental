package com.example.web_seguro;

import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RecetasControllerTest {

    @Test
    public void testGetRecetas_ConNombrePersonalizado() {
        // ARRANGE
        RecetasController controller = new RecetasController();
        Model model = new ExtendedModelMap();
        String nombreEsperado = "Juan";

        // ACT
        String vista = controller.getRecetas(nombreEsperado, model);

        // ASSERT
        assertEquals("recetas", vista);
        assertEquals(nombreEsperado, model.getAttribute("name"));
    }

    @Test
    public void testGetRecetas_ValorDefault() {
        // ARRANGE
        RecetasController controller = new RecetasController();
        Model model = new ExtendedModelMap();

        String defaultName = "Seguridad y Calidad en el Desarrollo";

        // ACT
        String vista = controller.getRecetas(defaultName, model);

        // ASSERT
        assertEquals("recetas", vista);
        assertEquals(
                "Seguridad y Calidad en el Desarrollo",
                model.getAttribute("name"),
                "Debe usar el valor por defecto indicado en RequestParam"
        );
    }
}
