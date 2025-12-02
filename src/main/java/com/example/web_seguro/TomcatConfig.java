package com.example.web_seguro;

import org.apache.coyote.ProtocolHandler;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatConfig {
    @Bean
    public TomcatServletWebServerFactory tomcatCustomizer() {
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();

        factory.addConnectorCustomizers((TomcatConnectorCustomizer) connector -> {
            ProtocolHandler protocolHandler = connector.getProtocolHandler();
            connector.setXpoweredBy(false);// aqui oculta X-Powered-By
        });

        return factory;
    }
}
