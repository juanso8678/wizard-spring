package com.wizard.core.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("WIZARD API")
                .version("1.0")
                .description("Documentación de la API para WIZARD")
                .contact(new Contact().name("Equipo WIZARD").email("soporte@wizard.com"))
            );
    }
}

