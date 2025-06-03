package com.wizard.core.modules.pacs.service.impl;

import com.wizard.core.config.TenantContext;
import com.wizard.core.exception.NotFoundException;
import com.wizard.core.exception.UnauthorizedException;
import com.wizard.core.exception.ValidationException;
import com.wizard.core.modules.pacs.dto.DicomNodeRequest;
import com.wizard.core.modules.pacs.dto.DicomNodeResponse;
import com.wizard.core.modules.pacs.entity.DicomNode;
import com.wizard.core.modules.pacs.mapper.DicomNodeMapper;
import com.wizard.core.modules.pacs.repository.DicomNodeRepository;
import com.wizard.core.modules.pacs.service.interfaces.DicomNodeService;
import com.wizard.core.modules.pacs.integration.WizardPacsEngineService; // ✅ NUEVO NOMBRE
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DicomNodeServiceImpl implements DicomNodeService {

    private final DicomNodeRepository dicomNodeRepository;
    private final DicomNodeMapper dicomNodeMapper;
    private final WizardPacsEngineService wizardPacsEngineService; // ✅ NUEVO SERVICIO

    @Override
    @Transactional
    public DicomNodeResponse createDicomNode(DicomNodeRequest request) {
        UUID currentOrg = TenantContext.getOrganizationId();
        
        // Validar organización
        if (currentOrg != null && request.getOrganizationId() != null && 
            !currentOrg.equals(request.getOrganizationId())) {
            throw new UnauthorizedException("No puede crear nodos DICOM en otra organización");
        }
        
        if (currentOrg != null && request.getOrganizationId() == null) {
            request.setOrganizationId(currentOrg);
        }
        
        // Validar que no exista AE Title duplicado en la organización
        if (dicomNodeRepository.existsByAeTitleAndOrganizationId(request.getAeTitle(), request.getOrganizationId())) {
            throw new ValidationException("Ya existe un nodo DICOM con ese AE Title en esta organización");
        }
        
        DicomNode node = dicomNodeMapper.toEntity(request);
        DicomNode saved = dicomNodeRepository.save(node);
        
        // ✅ SINCRONIZAR CON WIZARD PACS ENGINE
        try {
            boolean success = wizardPacsEngineService.createAETitle(saved);
            if (success) {
                log.info("🔗 [WIZARD PACS] Node created and synced with PACS Engine: {} for org: {}", 
                        saved.getAeTitle(), saved.getOrganizationId());
            } else {
                log.warn("⚠️ [WIZARD PACS] Node created but sync with PACS Engine failed: {}", saved.getAeTitle());
            }
        } catch (Exception e) {
            log.error("❌ [WIZARD PACS] Failed to sync with PACS Engine: {}", e.getMessage());
            // No fallar la operación, solo loggear
        }
        
        return dicomNodeMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DicomNodeResponse> findAllDicomNodes() {
        UUID currentOrg = TenantContext.getOrganizationId();
        
        if (currentOrg != null) {
            return dicomNodeRepository.findByOrganizationId(currentOrg).stream()
                    .map(dicomNodeMapper::toResponse)
                    .collect(Collectors.toList());
        } else {
            // Super admin - ve todos los nodos
            return dicomNodeRepository.findAll().stream()
                    .map(dicomNodeMapper::toResponse)
                    .collect(Collectors.toList());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public DicomNodeResponse findDicomNodeById(UUID id) {
        UUID currentOrg = TenantContext.getOrganizationId();
        
        DicomNode node = dicomNodeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Nodo DICOM no encontrado"));
        
        // Validar acceso por organización
        if (currentOrg != null && !currentOrg.equals(node.getOrganizationId())) {
            throw new UnauthorizedException("No tiene acceso a este nodo DICOM");
        }
        
        return dicomNodeMapper.toResponse(node);
    }

    @Override
    @Transactional
    public DicomNodeResponse updateDicomNode(UUID id, DicomNodeRequest request) {
        UUID currentOrg = TenantContext.getOrganizationId();
        
        DicomNode node = dicomNodeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Nodo DICOM no encontrado"));
        
        // Validar acceso por organización
        if (currentOrg != null && !currentOrg.equals(node.getOrganizationId())) {
            throw new UnauthorizedException("No puede modificar nodos DICOM de otra organización");
        }
        
        // Actualizar campos
        node.setHostname(request.getHostname());
        node.setPort(request.getPort());
        node.setDescription(request.getDescription());
        node.setQueryRetrieve(request.getQueryRetrieve());
        node.setStore(request.getStore());
        node.setEcho(request.getEcho());
        node.setFind(request.getFind());
        node.setMove(request.getMove());
        node.setGet(request.getGet());
        
        DicomNode saved = dicomNodeRepository.save(node);
        
        // ✅ SINCRONIZAR CAMBIOS CON WIZARD PACS ENGINE
        try {
            boolean success = wizardPacsEngineService.updateAETitle(saved);
            if (success) {
                log.info("🔄 [WIZARD PACS] Node updated and synced: {}", saved.getAeTitle());
            } else {
                log.warn("⚠️ [WIZARD PACS] Node updated but sync failed: {}", saved.getAeTitle());
            }
        } catch (Exception e) {
            log.error("❌ [WIZARD PACS] Failed to sync update with PACS Engine: {}", e.getMessage());
        }
        
        return dicomNodeMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteDicomNode(UUID id) {
        UUID currentOrg = TenantContext.getOrganizationId();
        
        DicomNode node = dicomNodeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Nodo DICOM no encontrado"));
        
        // Validar acceso por organización
        if (currentOrg != null && !currentOrg.equals(node.getOrganizationId())) {
            throw new UnauthorizedException("No puede eliminar nodos DICOM de otra organización");
        }
        
        // ✅ ELIMINAR DE WIZARD PACS ENGINE PRIMERO
        try {
            boolean success = wizardPacsEngineService.deleteAETitle(node.getAeTitle());
            if (success) {
                log.info("🗑️ [WIZARD PACS] Node removed from PACS Engine: {}", node.getAeTitle());
            } else {
                log.warn("⚠️ [WIZARD PACS] Failed to remove from PACS Engine: {}", node.getAeTitle());
            }
        } catch (Exception e) {
            log.error("❌ [WIZARD PACS] Failed to remove from PACS Engine: {}", e.getMessage());
        }
        
        dicomNodeRepository.deleteById(id);
        log.info("🗑️ [WIZARD PACS] Node deleted from database: {}", node.getAeTitle());
    }

    @Override
    public boolean performEcho(UUID nodeId) {
        UUID currentOrg = TenantContext.getOrganizationId();
        
        DicomNode node = dicomNodeRepository.findById(nodeId)
                .orElseThrow(() -> new NotFoundException("Nodo DICOM no encontrado"));
        
        // Validar acceso
        if (currentOrg != null && !currentOrg.equals(node.getOrganizationId())) {
            throw new UnauthorizedException("No tiene acceso a este nodo DICOM");
        }
        
        try {
            // ✅ USAR WIZARD PACS ENGINE PARA C-ECHO
            boolean echoResult = wizardPacsEngineService.performEcho(node);
            log.info("📡 [WIZARD PACS] Echo {} for node: {}", 
                    echoResult ? "SUCCESS" : "FAILED", node.getAeTitle());
            return echoResult;
        } catch (Exception e) {
            log.error("❌ [WIZARD PACS] Echo failed for node {}: {}", node.getAeTitle(), e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<DicomNodeResponse> findActiveNodes() {
        UUID currentOrg = TenantContext.getOrganizationId();
        
        if (currentOrg != null) {
            return dicomNodeRepository.findByOrganizationIdAndActiveTrue(currentOrg).stream()
                    .map(dicomNodeMapper::toResponse)
                    .collect(Collectors.toList());
        } else {
            return dicomNodeRepository.findAll().stream()
                    .filter(node -> node.getActive())
                    .map(dicomNodeMapper::toResponse)
                    .collect(Collectors.toList());
        }
    }

    @Override
    @Transactional
    public DicomNodeResponse toggleActiveStatus(UUID nodeId) {
        UUID currentOrg = TenantContext.getOrganizationId();
        
        DicomNode node = dicomNodeRepository.findById(nodeId)
                .orElseThrow(() -> new NotFoundException("Nodo DICOM no encontrado"));
        
        // Validar acceso por organización
        if (currentOrg != null && !currentOrg.equals(node.getOrganizationId())) {
            throw new UnauthorizedException("No tiene acceso a este nodo DICOM");
        }
        
        // Toggle status
        node.setActive(!node.getActive());
        DicomNode saved = dicomNodeRepository.save(node);
        
        log.info("🔄 [WIZARD PACS] Node {} {}: {}", 
                saved.getActive() ? "activated" : "deactivated", 
                saved.getAeTitle(), saved.getActive());
        
        return dicomNodeMapper.toResponse(saved);
    }

    @Override
    public Map<String, Boolean> performBatchEcho() {
        UUID currentOrg = TenantContext.getOrganizationId();
        
        List<DicomNode> activeNodes;
        if (currentOrg != null) {
            activeNodes = dicomNodeRepository.findByOrganizationIdAndActiveTrue(currentOrg);
        } else {
            activeNodes = dicomNodeRepository.findAll().stream()
                    .filter(DicomNode::getActive)
                    .collect(Collectors.toList());
        }
        
        try {
            // ✅ USAR WIZARD PACS ENGINE PARA BATCH ECHO ASÍNCRONO
            Map<String, Boolean> results = wizardPacsEngineService
                    .performBatchEchoAsync(activeNodes)
                    .get(); // Esperar resultado
            
            log.info("📡 [WIZARD PACS] Batch echo completed for {} nodes", results.size());
            return results;
        } catch (Exception e) {
            log.error("❌ [WIZARD PACS] Batch echo failed: {}", e.getMessage());
            
            // Fallback: echo secuencial
            Map<String, Boolean> results = new HashMap<>();
            for (DicomNode node : activeNodes) {
                try {
                    boolean echoResult = wizardPacsEngineService.performEcho(node);
                    results.put(node.getAeTitle(), echoResult);
                } catch (Exception ex) {
                    results.put(node.getAeTitle(), false);
                    log.warn("📡 [BATCH ECHO FALLBACK] {} failed: {}", node.getAeTitle(), ex.getMessage());
                }
            }
            
            return results;
        }
    }
    
    /**
     * ✅ NUEVOS MÉTODOS ESPECÍFICOS DE WIZARD PACS
     */
    
    /**
     * Sincroniza todos los nodos locales con el PACS Engine
     */
    @Transactional
    public Map<String, Boolean> syncAllNodesToEngine() {
        UUID currentOrg = TenantContext.getOrganizationId();
        
        List<DicomNode> nodes;
        if (currentOrg != null) {
            nodes = dicomNodeRepository.findByOrganizationId(currentOrg);
        } else {
            nodes = dicomNodeRepository.findAll();
        }
        
        Map<String, Boolean> results = new HashMap<>();
        
        for (DicomNode node : nodes) {
            try {
                boolean success = wizardPacsEngineService.createAETitle(node);
                results.put(node.getAeTitle(), success);
                
                if (success) {
                    log.info("🔄 [WIZARD PACS] Node synced to engine: {}", node.getAeTitle());
                } else {
                    log.warn("⚠️ [WIZARD PACS] Failed to sync node: {}", node.getAeTitle());
                }
            } catch (Exception e) {
                results.put(node.getAeTitle(), false);
                log.error("❌ [WIZARD PACS] Error syncing node {}: {}", node.getAeTitle(), e.getMessage());
            }
        }
        
        log.info("🔄 [WIZARD PACS] Sync completed for {} nodes", results.size());
        return results;
    }
    
    /**
     * Verifica el estado de sincronización con el PACS Engine
     */
    public Map<String, Object> checkEngineSync() {
        try {
            // Obtener nodos locales
            UUID currentOrg = TenantContext.getOrganizationId();
            List<DicomNode> localNodes;
            if (currentOrg != null) {
                localNodes = dicomNodeRepository.findByOrganizationId(currentOrg);
            } else {
                localNodes = dicomNodeRepository.findAll();
            }
            
            // Obtener AE Titles del engine
            List<Map<String, Object>> engineAeTitles = wizardPacsEngineService.listAETitles();
            
            // Comparar
            List<String> localAeTitles = localNodes.stream()
                    .map(DicomNode::getAeTitle)
                    .collect(Collectors.toList());
            
            List<String> engineAeTitlesList = engineAeTitles.stream()
                    .map(ae -> (String) ae.get("dicomAETitle"))
                    .collect(Collectors.toList());
            
            // Encontrar diferencias
            List<String> missingInEngine = localAeTitles.stream()
                    .filter(ae -> !engineAeTitlesList.contains(ae))
                    .collect(Collectors.toList());
            
            List<String> extraInEngine = engineAeTitlesList.stream()
                    .filter(ae -> !localAeTitles.contains(ae))
                    .filter(ae -> !ae.equals("WIZARD_PACS")) // Excluir AE Title principal
                    .collect(Collectors.toList());
            
            boolean inSync = missingInEngine.isEmpty() && extraInEngine.isEmpty();
            
            Map<String, Object> syncStatus = new HashMap<>();
            syncStatus.put("inSync", inSync);
            syncStatus.put("localNodesCount", localNodes.size());
            syncStatus.put("engineAeTitlesCount", engineAeTitles.size());
            syncStatus.put("missingInEngine", missingInEngine);
            syncStatus.put("extraInEngine", extraInEngine);
            syncStatus.put("timestamp", System.currentTimeMillis());
            
            log.info("🔍 [WIZARD PACS] Sync check completed - In sync: {}", inSync);
            
            return syncStatus;
        } catch (Exception e) {
            log.error("❌ [WIZARD PACS] Sync check failed: {}", e.getMessage());
            return Map.of(
                "inSync", false,
                "error", e.getMessage(),
                "timestamp", System.currentTimeMillis()
            );
        }
    }
    
    /**
     * Obtiene estadísticas del PACS Engine
     */
    public Map<String, Object> getEngineStatistics() {
        try {
            Map<String, Object> stats = wizardPacsEngineService.getStatistics();
            log.debug("📊 [WIZARD PACS] Engine statistics retrieved");
            return stats;
        } catch (Exception e) {
            log.error("❌ [WIZARD PACS] Failed to get engine statistics: {}", e.getMessage());
            return Map.of(
                "error", e.getMessage(),
                "timestamp", System.currentTimeMillis()
            );
        }
    }
    
    /**
     * Realiza mantenimiento del PACS Engine
     */
    public Map<String, Object> performEngineMaintenance() {
        Map<String, Object> maintenanceResult = new HashMap<>();
        
        try {
            log.info("🔧 [WIZARD PACS] Starting engine maintenance...");
            
            // 1. Test de conectividad
            boolean connected = wizardPacsEngineService.testConnection();
            maintenanceResult.put("connectionTest", connected);
            
            if (!connected) {
                maintenanceResult.put("status", "failed");
                maintenanceResult.put("error", "No connection to PACS Engine");
                return maintenanceResult;
            }
            
            // 2. Sincronización de nodos
            Map<String, Boolean> syncResults = syncAllNodesToEngine();
            maintenanceResult.put("syncResults", syncResults);
            
            long successfulSyncs = syncResults.values().stream()
                    .mapToLong(success -> success ? 1 : 0)
                    .sum();
            
            // 3. Verificación de estado
            Map<String, Object> syncStatus = checkEngineSync();
            maintenanceResult.put("syncStatus", syncStatus);
            
            // 4. Estadísticas
            Map<String, Object> stats = getEngineStatistics();
            maintenanceResult.put("statistics", stats);
            
            // 5. Resultado final
            boolean maintenanceSuccess = connected && 
                    successfulSyncs == syncResults.size() &&
                    (Boolean) syncStatus.get("inSync");
            
            maintenanceResult.put("status", maintenanceSuccess ? "success" : "partial");
            maintenanceResult.put("timestamp", System.currentTimeMillis());
            
            log.info("🔧 [WIZARD PACS] Maintenance completed - Status: {}", 
                    maintenanceSuccess ? "SUCCESS" : "PARTIAL");
            
            return maintenanceResult;
        } catch (Exception e) {
            log.error("❌ [WIZARD PACS] Maintenance failed: {}", e.getMessage());
            maintenanceResult.put("status", "failed");
            maintenanceResult.put("error", e.getMessage());
            maintenanceResult.put("timestamp", System.currentTimeMillis());
            return maintenanceResult;
        }
    }
}

// ============================================================================
// MÉTODOS ADICIONALES PARA EL CONTROLLER
// ============================================================================

/*
 * Estos métodos pueden ser agregados al DicomNodeController para 
 * exponer las nuevas funcionalidades:
 */

/*
@PostMapping("/sync-to-engine")
@RequiresPermission(PermissionCode.ADMIN_ACCESS)
@Operation(summary = "Sincronizar todos los nodos con PACS Engine")
public ResponseEntity<Map<String, Boolean>> syncNodesToEngine() {
    Map<String, Boolean> results = dicomNodeService.syncAllNodesToEngine();
    return ResponseEntity.ok(results);
}

@GetMapping("/engine-sync-status")
@RequiresPermission(PermissionCode.ENRUTAMIENTO_VIEW)
@Operation(summary = "Verificar estado de sincronización con PACS Engine")
public ResponseEntity<Map<String, Object>> getEngineSyncStatus() {
    Map<String, Object> syncStatus = dicomNodeService.checkEngineSync();
    return ResponseEntity.ok(syncStatus);
}

@GetMapping("/engine-stats")
@RequiresPermission(PermissionCode.ADMIN_ACCESS)
@Operation(summary = "Obtener estadísticas del PACS Engine")
public ResponseEntity<Map<String, Object>> getEngineStats() {
    Map<String, Object> stats = dicomNodeService.getEngineStatistics();
    return ResponseEntity.ok(stats);
}

@PostMapping("/maintenance")
@RequiresPermission(PermissionCode.ADMIN_ACCESS)
@Operation(summary = "Realizar mantenimiento del PACS Engine")
public ResponseEntity<Map<String, Object>> performMaintenance() {
    Map<String, Object> result = dicomNodeService.performEngineMaintenance();
    return ResponseEntity.ok(result);
}
*/