package com.example.web_seguro.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;


import org.junit.jupiter.api.Test;


class EmpresaModeloTest {
 
      @Test
    void testPrePersistGeneratesUUIDWhenNull() {
        Empresa empresa = new Empresa();

        assertNull(empresa.getUuid()); // antes debe estar null

        empresa.prePersist();

        assertNotNull(empresa.getUuid());
        assertFalse(empresa.getUuid().isEmpty());
    }

    @Test
    void testPrePersistDoesNotOverrideExistingUUID() {
        Empresa empresa = new Empresa();
        empresa.setUuid("EXISTING-UUID-123");

        empresa.prePersist();

        assertEquals("EXISTING-UUID-123", empresa.getUuid()); // NO debe cambiarse
    }


}
