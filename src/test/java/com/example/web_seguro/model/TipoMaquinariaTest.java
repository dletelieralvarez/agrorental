package com.example.web_seguro.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class TipoMaquinariaTest {
   @Test
    void testPrePersistGeneratesUUIDWhenNull() {
        TipoMaquinaria tipo = new TipoMaquinaria();

        assertNull(tipo.getUuid()); // inicialmente null

        tipo.prePersist();

        assertNotNull(tipo.getUuid());        // ahora debe estar lleno
        assertFalse(tipo.getUuid().isEmpty()); // no debe estar vacío
    }

    @Test
    void testPrePersistDoesNotOverrideExistingUUID() {
        TipoMaquinaria tipo = new TipoMaquinaria();
        tipo.setUuid("EXISTING-UUID-789");

        tipo.prePersist();

        assertEquals("EXISTING-UUID-789", tipo.getUuid()); // debe mantenerse
    }
}
