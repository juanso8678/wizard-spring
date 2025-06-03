package com.wizard.core.modules.pacs.controller;

import com.wizard.core.enums.PermissionCode;
import com.wizard.core.modules.pacs.dto.StudySearchCriteria;
import com.wizard.core.modules.pacs.entity.DicomNode;
import com.wizard.core.modules.pacs.health.WizardPacsEngineHealthCheck;
import com.wizard.core.modules.pacs.integration.WizardPacsEngineService;
import com.wizard.core.security.annotation.RequiresPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/pacs/engine")
@RequiredArgsConstructor
@Tag(name = "PACS Engine", description = "Gestión del motor PACS integrado")
public class WizardPacsEngineController {

    private final WizardPacsEngineService pacsEngineService;

    @GetMapping("/status")
    @RequiresPermission(PermissionCode.PACS_VIEW)
    @Operation(summary = "Estado del PACS Engine")
    public ResponseEntity<Map<String, Object>> getEngineStatus() {
        Map<String, Object> status = pacsEngineService.getStatistics();
        return ResponseEntity.ok(status);
    }

    @GetMapping("/system-info")
    @RequiresPermission(PermissionCode.ADMIN_ACCESS)
    @Operation(summary = "Información del sistema PACS")
    public ResponseEntity<Map<String, Object>> getSystemInfo() {
        Map<String, Object> systemInfo = pacsEngineService.getSystemInfo();
        return ResponseEntity.ok(systemInfo);
    }

    @PostMapping("/test-connection")
    @RequiresPermission(PermissionCode.PACS_VIEW)
    @Operation(summary = "Probar conexión con PACS Engine")
    public ResponseEntity<Map<String, Object>> testConnection() {
        boolean connected = pacsEngineService.testConnection();
        return ResponseEntity.ok(Map.of(
            "connected", connected,
            "timestamp", System.currentTimeMillis()
        ));
    }

    @GetMapping("/ae-titles")
    @RequiresPermission(PermissionCode.ENRUTAMIENTO_VIEW)
    @Operation(summary = "Listar AE Titles del engine")
    public ResponseEntity<List<Map<String, Object>>> listAETitles() {
        List<Map<String, Object>> aeTitles = pacsEngineService.listAETitles();
        return ResponseEntity.ok(aeTitles);
    }

    @PostMapping("/echo/{aeTitle}")
    @RequiresPermission(PermissionCode.ENRUTAMIENTO_VIEW)
    @Operation(summary = "Realizar C-ECHO a AE Title específico")
    public ResponseEntity<Map<String, Object>> performEcho(@PathVariable String aeTitle) {
        boolean success = pacsEngineService.performEcho(aeTitle, "WIZARD_PACS");
        return ResponseEntity.ok(Map.of(
            "aeTitle", aeTitle,
            "success", success,
            "timestamp", System.currentTimeMillis()
        ));
    }

    @PostMapping("/batch-echo")
    @RequiresPermission(PermissionCode.ENRUTAMIENTO_VIEW)
    @Operation(summary = "Realizar C-ECHO masivo asíncrono")
    public ResponseEntity<CompletableFuture<Map<String, Boolean>>> performBatchEcho(
            @RequestBody List<DicomNode> nodes) {
        CompletableFuture<Map<String, Boolean>> results = pacsEngineService.performBatchEchoAsync(nodes);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/studies/search")
    @RequiresPermission(PermissionCode.PACS_VIEW)
    @Operation(summary = "Buscar estudios en PACS Engine")
    public ResponseEntity<List<Map<String, Object>>> searchStudies(
            @RequestParam(required = false) String patientId,
            @RequestParam(required = false) String patientName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate studyDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate studyDateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate studyDateTo,
            @RequestParam(required = false) String modality,
            @RequestParam(required = false) String accessionNumber,
            @RequestParam(defaultValue = "50") Integer limit,
            @RequestParam(defaultValue = "0") Integer offset) {
        
        StudySearchCriteria criteria = StudySearchCriteria.builder()
                .patientId(patientId)
                .patientName(patientName)
                .studyDate(studyDate)
                .studyDateFrom(studyDateFrom)
                .studyDateTo(studyDateTo)
                .modality(modality)
                .accessionNumber(accessionNumber)
                .limit(limit)
                .offset(offset)
                .build();
        
        List<Map<String, Object>> studies = pacsEngineService.performStudyFind(criteria);
        return ResponseEntity.ok(studies);
    }

    @GetMapping("/studies/{studyInstanceUID}")
    @RequiresPermission(PermissionCode.PACS_VIEW)
    @Operation(summary = "Obtener detalles de estudio")
    public ResponseEntity<Map<String, Object>> getStudyDetails(
            @PathVariable String studyInstanceUID) {
        Map<String, Object> study = pacsEngineService.getStudyDetails(studyInstanceUID);
        return ResponseEntity.ok(study);
    }

    @GetMapping("/studies/{studyInstanceUID}/series")
    @RequiresPermission(PermissionCode.PACS_VIEW)
    @Operation(summary = "Obtener series de estudio")
    public ResponseEntity<List<Map<String, Object>>> getStudySeries(
            @PathVariable String studyInstanceUID) {
        List<Map<String, Object>> series = pacsEngineService.getStudySeries(studyInstanceUID);
        return ResponseEntity.ok(series);
    }

    @GetMapping("/studies/{studyInstanceUID}/series/{seriesInstanceUID}/instances")
    @RequiresPermission(PermissionCode.PACS_VIEW)
    @Operation(summary = "Obtener instancias de serie")
    public ResponseEntity<List<Map<String, Object>>> getSeriesInstances(
            @PathVariable String studyInstanceUID,
            @PathVariable String seriesInstanceUID) {
        List<Map<String, Object>> instances = pacsEngineService.getSeriesInstances(
                studyInstanceUID, seriesInstanceUID);
        return ResponseEntity.ok(instances);
    }

    @PostMapping("/studies/{studyInstanceUID}/move")
    @RequiresPermission(PermissionCode.PACS_EDIT)
    @Operation(summary = "Mover estudio a destino")
    public ResponseEntity<Map<String, Object>> moveStudy(
            @PathVariable String studyInstanceUID,
            @RequestParam String destinationAET) {
        boolean success = pacsEngineService.performStudyMove(studyInstanceUID, destinationAET);
        return ResponseEntity.ok(Map.of(
            "studyInstanceUID", studyInstanceUID,
            "destinationAET", destinationAET,
            "success", success,
            "timestamp", System.currentTimeMillis()
        ));
    }

    @DeleteMapping("/studies/{studyInstanceUID}")
    @RequiresPermission(PermissionCode.PACS_DELETE)
    @Operation(summary = "Eliminar estudio del PACS Engine")
    public ResponseEntity<Map<String, Object>> deleteStudy(
            @PathVariable String studyInstanceUID) {
        boolean success = pacsEngineService.deleteStudy(studyInstanceUID);
        return ResponseEntity.ok(Map.of(
            "studyInstanceUID", studyInstanceUID,
            "deleted", success,
            "timestamp", System.currentTimeMillis()
        ));
    }

    @GetMapping("/storage/stats")
    @RequiresPermission(PermissionCode.ADMIN_ACCESS)
    @Operation(summary = "Estadísticas de almacenamiento")
    public ResponseEntity<Map<String, Object>> getStorageStats() {
        Map<String, Object> storageStats = pacsEngineService.getStorageStatistics();
        return ResponseEntity.ok(storageStats);
    }

    @GetMapping("/health")
@RequiresPermission(PermissionCode.PACS_VIEW)
@Operation(summary = "Health check del PACS Engine")
public ResponseEntity<Map<String, Object>> healthCheck() {
    // Usar el health checker simplificado
    WizardPacsEngineHealthCheck healthCheck = new WizardPacsEngineHealthCheck(pacsEngineService);
    Map<String, Object> health = healthCheck.checkHealth();
    
    HttpStatus status = "UP".equals(health.get("status")) ? 
                       HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE;
    
    return ResponseEntity.status(status).body(health);
}
}