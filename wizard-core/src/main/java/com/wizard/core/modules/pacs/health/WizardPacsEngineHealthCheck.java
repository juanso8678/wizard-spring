package com.wizard.core.modules.pacs.health;

import com.wizard.core.modules.pacs.integration.WizardPacsEngineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.HashMap;

/**
 * Health indicator simple SIN Spring Boot Actuator
 */
@Component
@RequiredArgsConstructor
public class WizardPacsEngineHealthCheck {

    private final WizardPacsEngineService pacsEngineService;

    public Map<String, Object> checkHealth() {
        Map<String, Object> health = new HashMap<>();
        
        try {
            boolean connected = pacsEngineService.testConnection();
            
            health.put("status", connected ? "UP" : "DOWN");
            health.put("connected", connected);
            health.put("timestamp", System.currentTimeMillis());
            
            if (connected) {
                Map<String, Object> systemInfo = pacsEngineService.getSystemInfo();
                health.put("systemInfo", systemInfo);
            }
            
            return health;
        } catch (Exception e) {
            health.put("status", "DOWN");
            health.put("error", e.getMessage());
            health.put("timestamp", System.currentTimeMillis());
            return health;
        }
    }
}