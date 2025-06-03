// ============================================================================
// 2. PacsConfig.java - Configuración actualizada para Wizard PACS Engine
// ============================================================================
package com.wizard.core.modules.pacs.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "wizard.pacs")
public class PacsConfig {
    
    /**
     * Configuración del engine PACS (DCM4CHEE backend)
     */
    private EngineConfig engine = new EngineConfig();
    
    /**
     * Configuración del visor DICOM
     */
    private ViewerConfig viewer = new ViewerConfig();
    
    /**
     * Configuración de almacenamiento
     */
    private StorageConfig storage = new StorageConfig();
    
    /**
     * Configuración de integración
     */
    private IntegrationConfig integration = new IntegrationConfig();
    
    @Data
    public static class EngineConfig {
        private String name = "Wizard PACS Engine";
        private String url = "http://wizard-pacs-engine:8080";
        private String username = "wizard_admin";
        private String password = "wizard_pacs_2024";
        private String defaultAeTitle = "WIZARD_PACS";
        private Integer timeoutSeconds = 30;
        private Integer retryAttempts = 3;
        private Boolean autoInit = true;
        private String version = "2.0.0";
        private String vendor = "Wizard Healthcare Solutions";
    }
    
    @Data
    public static class ViewerConfig {
        private String baseUrl = "http://localhost:3000";
        private String title = "Wizard DICOM Viewer";
        private Integer sessionTimeoutMinutes = 30;
        private Boolean enableFullscreen = true;
        private Boolean showStudyList = true;
        private Integer maxWebWorkers = 3;
    }
    
    @Data
    public static class StorageConfig {
        private String location = "/shared/wizard-storage";
        private String engineStorage = "/shared/pacs-storage";
        private Long maxFileSize = 1024L * 1024L * 1024L; // 1GB
        private String[] allowedExtensions = {"dcm", "dicom"};
        private Boolean autoCleanup = true;
        private Integer retentionDays = 365;
    }
    
    @Data
    public static class IntegrationConfig {
        private Boolean autoCreateAeTitles = true;
        private Boolean syncStudies = true;
        private Boolean enableEchoMonitoring = true;
        private String branding = "Wizard PACS Engine";
        private Integer batchEchoInterval = 300; // segundos
        private Boolean enableAuditLog = true;
    }
}