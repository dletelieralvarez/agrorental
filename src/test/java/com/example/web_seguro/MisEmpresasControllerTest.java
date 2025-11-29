package com.example.web_seguro;

import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MisEmpresasControllerTest {

    @Test
    public void testGetMisEmpresas_ConNombrePersonalizado() {
        // ARRANGE
        MisEmpresasController controller = new MisEmpresasController();
        Model model = new ExtendedModelMap();
        String nombreEsperado = "Juan Pérez";

        // ACT
        String vista = controller.getMisEmpresas(nombreEsperado, model);

        // ASSERT
        assertEquals("mis_empresas", vista);
        assertEquals(nombreEsperado, model.getAttribute("name"));
    }

    @Test
    public void testGetMisEmpresas_ValorDefault() {
        // ARRANGE
        MisEmpresasController controller = new MisEmpresasController();
        Model model = new ExtendedModelMap();

        String defaultName = "Seguridad y Calidad en el Desarrollo";

        // ACT
        String vista = controller.getMisEmpresas(defaultName, model);

        // ASSERT
        assertEquals("mis_empresas", vista);
        assertEquals("Seguridad y Calidad en el Desarrollo",
                model.getAttribute("name"),
                "Debe usar el valor por defecto del RequestParam");
    }
}
