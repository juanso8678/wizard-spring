package com.wizard.core.modules.pacs.event;

import com.wizard.core.modules.pacs.integration.WizardPacsEngineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class WizardPacsEngineEventListener {

    private final WizardPacsEngineService pacsEngineService;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @EventListener
    public void onApplicationReady(ApplicationReadyEvent event) {
        log.info("🚀 [WIZARD PACS ENGINE] Application ready, testing connection...");
        
        // Ejecutar en hilo separado para no bloquear el startup
        scheduler.schedule(() -> {
            try {
                // Esperar a que el engine termine de inicializar
                Thread.sleep(5000);
                
                boolean connected = pacsEngineService.testConnection();
                if (connected) {
                    log.info("✅ [WIZARD PACS ENGINE] Successfully connected to PACS Engine");
                    performInitialConfiguration();
                } else {
                    log.warn("⚠️ [WIZARD PACS ENGINE] Failed to connect to PACS Engine");
                }
            } catch (Exception e) {
                log.error("🚨 [WIZARD PACS ENGINE] Error during initialization: {}", e.getMessage());
            }
        }, 2, TimeUnit.SECONDS);
        
        // Health check periódico cada 5 minutos
        scheduler.scheduleAtFixedRate(() -> {
            try {
                boolean connected = pacsEngineService.testConnection();
                if (!connected) {
                    log.warn("⚠️ [WIZARD PACS ENGINE] Periodic health check failed");
                }
            } catch (Exception e) {
                log.error("🚨 [WIZARD PACS ENGINE] Health check error: {}", e.getMessage());
            }
        }, 5, 5, TimeUnit.MINUTES);
    }

    private void performInitialConfiguration() {
        try {
            log.info("⚙️ [WIZARD PACS ENGINE] Performing initial configuration...");
            // Configuración inicial aquí
            log.info("✅ [WIZARD PACS ENGINE] Initial configuration completed");
        } catch (Exception e) {
            log.error("🚨 [WIZARD PACS ENGINE] Initial configuration failed: {}", e.getMessage());
        }
    }
}