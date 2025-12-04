package com.example.web_seguro;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.ProtocolHandler;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import java.util.Collection;

public class TomcatConfigTest {

    @Test
    void testTomcatCustomizer_EjecutaLambdaYDesactivaXPoweredBy() {
        // ARRANGE: crea la config y obtiene el factory
        TomcatConfig config = new TomcatConfig();
        TomcatServletWebServerFactory factory = config.tomcatCustomizer();

        assertNotNull(factory);

        // mock del Connector y del ProtocolHandler
        Connector connector = mock(Connector.class);
        ProtocolHandler protocolHandler = mock(ProtocolHandler.class);
        when(connector.getProtocolHandler()).thenReturn(protocolHandler);

        // obtiene los customizers que se registran
        Collection<TomcatConnectorCustomizer> customizers =
                factory.getTomcatConnectorCustomizers();

        assertFalse(customizers.isEmpty(), "Debe haber al menos un customizer");
        
        // ACT: ejecuta todos los customizers sobre el connector mock
        for (TomcatConnectorCustomizer customizer : customizers) {
            customizer.customize(connector);
        }

        // ASSERT: se llamó a getProtocolHandler y se desactivó X-Powered-By
        verify(connector, atLeastOnce()).getProtocolHandler();
        verify(connector).setXpoweredBy(false);
    }
}
