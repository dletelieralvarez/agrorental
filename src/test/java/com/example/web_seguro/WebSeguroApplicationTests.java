package com.example.web_seguro;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class WebSeguroApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
    void mainDebeEjecutarseSinErrores() {        
        WebSeguroApplication.main(new String[] {});
    }
}
