package com.wizard.core.modules.pacs.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

@Configuration
@EnableRetry
public class RetryConfiguration {
    // Configuración para @Retryable en WizardPacsEngineService
}