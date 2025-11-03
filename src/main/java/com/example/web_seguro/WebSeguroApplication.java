package com.example.web_seguro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class WebSeguroApplication {

	private static final Logger logger = LoggerFactory.getLogger(WebSeguroApplication.class);

	public static void main(String[] args) {
		 logger.info("🚀 Iniciando aplicación...");

		SpringApplication.run(WebSeguroApplication.class, args);
	}

}
