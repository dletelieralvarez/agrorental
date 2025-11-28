package com.example.web_seguro;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ContactoControllerTest {
    
    @Test
    public void testGetContact_ConNombrePersonalizado() {
        // ARRANGE
        ContactoController controller = new ContactoController();
        Model model = new ExtendedModelMap();
        String nombreEsperado = "Juan";

        // ACT
        String vista = controller.getMisEmpresas(nombreEsperado, model);

        // ASSERT
        assertEquals("contact", vista, "La vista debe llamarse 'contact'");
        assertEquals(nombreEsperado, model.getAttribute("name"),
                "El atributo 'name' debe contener el valor enviado");
    }

    @Test
    public void testGetContact_UsaValorDefault() {
        // ARRANGE
        ContactoController controller = new ContactoController();
        Model model = new ExtendedModelMap();

        // ACT
        String vista = controller.getMisEmpresas(null, model);

        // ASSERT
        assertEquals("contact", vista);
        assertEquals("Seguridad y Calidad en el Desarrollo",
                model.getAttribute("name"),
                "Debe aplicar el default definido en RequestParam");
    }
}
