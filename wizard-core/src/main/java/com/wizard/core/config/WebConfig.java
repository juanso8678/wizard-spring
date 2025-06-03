package com.wizard.core.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración web para registrar interceptors
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    
    private final TenantInterceptor tenantInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tenantInterceptor)
                .addPathPatterns("/api/**")              // Aplicar a todos los endpoints de API
                .excludePathPatterns(
                    "/api/auth/**",                      // Excluir auth
                    "/ping",                             // Excluir ping
                    "/swagger-ui/**",                    // Excluir swagger
                    "/v3/api-docs/**",                   // Excluir docs
                    "/api/test/**"                       // Excluir tests (temporal)
                );
    }
}