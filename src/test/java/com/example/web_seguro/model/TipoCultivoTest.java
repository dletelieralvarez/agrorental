package com.example.web_seguro.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class TipoCultivoTest {

    @Test
    void testPrePersistGeneratesUUIDWhenNull() {
        TipoCultivo tipo = new TipoCultivo();

        assertNull(tipo.getUuid()); // antes null

        tipo.prePersist();

        assertNotNull(tipo.getUuid());   // ahora no debe ser null
        assertFalse(tipo.getUuid().isEmpty()); // debe tener contenido
    }

    @Test
    void testPrePersistDoesNotOverrideExistingUUID() {
        TipoCultivo tipo = new TipoCultivo();
        tipo.setUuid("EXISTING-UUID-456");

        tipo.prePersist();

        assertEquals("EXISTING-UUID-456", tipo.getUuid()); // No debe cambiarse
    }
}