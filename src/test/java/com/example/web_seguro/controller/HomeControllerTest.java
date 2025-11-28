package com.example.web_seguro.controller;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HomeControllerTest {
    
    @Test
    public void testIndex_ConNombrePersonalizado() {
        // ARRANGE
        HomeController controller = new HomeController();
        Model model = new ExtendedModelMap();

        String nombreEsperado = "Juan";

        // ACT
        String vista = controller.index(nombreEsperado, model);

        // ASSERT
        assertEquals("index", vista, "La vista debe llamarse 'index'");
        assertEquals(nombreEsperado, model.getAttribute("name"),
                "El atributo 'name' debe contener el valor enviado en la petición");
    }

    @Test
    public void testIndex_SinNombreUsandoDefault() {
        // ARRANGE
        HomeController controller = new HomeController();
        Model model = new ExtendedModelMap();

        // ACT
        String vista = controller.index(null, model);

        // ASSERT
        assertEquals("index", vista, "La vista debe llamarse 'index'");
        assertEquals("Seguridad y Calidad en el Desarrollo",
                model.getAttribute("name"),
                "Debe aplicar el valor por defecto definido en el controller");
    }
}
