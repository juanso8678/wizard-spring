package com.wizard.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * Configuración para clientes HTTP (RestTemplate, WebClient, etc.)
 */
@Configuration
public class RestClientConfig {

    /**
     * Bean RestTemplate para integraciones HTTP
     */
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        
        // Configurar timeouts
        restTemplate.setRequestFactory(clientHttpRequestFactory());
        
        return restTemplate;
    }
    
    /**
     * Configuración de timeouts para HTTP requests
     */
    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        
        // Timeout de conexión: 30 segundos
        factory.setConnectTimeout(Duration.ofSeconds(30));
        
        // Timeout de lectura: 60 segundos
        factory.setReadTimeout(Duration.ofSeconds(60));
        
        return factory;
    }
}