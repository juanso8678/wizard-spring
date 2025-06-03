package com.wizard.core.modules.pacs.controller;

import com.wizard.core.enums.PermissionCode;
import com.wizard.core.modules.pacs.dto.DicomNodeRequest;
import com.wizard.core.modules.pacs.dto.DicomNodeResponse;
import com.wizard.core.modules.pacs.service.interfaces.DicomNodeService;
import com.wizard.core.security.annotation.RequiresPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;  // ✅ IMPORT AGREGADO
import java.util.UUID;

@RestController
@RequestMapping("/api/pacs/dicom-nodes")
@RequiredArgsConstructor
@Tag(name = "PACS - Nodos DICOM", description = "Gestión de AE Titles y nodos DICOM")
public class DicomNodeController {

    private final DicomNodeService dicomNodeService;

    @PostMapping
    @RequiresPermission(PermissionCode.ENRUTAMIENTO_CREATE)
    @Operation(summary = "Crear nodo DICOM")
    public ResponseEntity<DicomNodeResponse> createDicomNode(@RequestBody DicomNodeRequest request) {
        return ResponseEntity.ok(dicomNodeService.createDicomNode(request));
    }

    @GetMapping
    @RequiresPermission(PermissionCode.ENRUTAMIENTO_VIEW)
    @Operation(summary = "Listar nodos DICOM")
    public ResponseEntity<List<DicomNodeResponse>> getAllDicomNodes() {
        return ResponseEntity.ok(dicomNodeService.findAllDicomNodes());
    }

    @GetMapping("/{id}")
    @RequiresPermission(PermissionCode.ENRUTAMIENTO_VIEW)
    @Operation(summary = "Obtener nodo DICOM por ID")
    public ResponseEntity<DicomNodeResponse> getDicomNodeById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(dicomNodeService.findDicomNodeById(id));
    }

    @PutMapping("/{id}")
    @RequiresPermission(PermissionCode.ENRUTAMIENTO_EDIT)
    @Operation(summary = "Actualizar nodo DICOM")
    public ResponseEntity<DicomNodeResponse> updateDicomNode(
            @PathVariable("id") UUID id, 
            @RequestBody DicomNodeRequest request) {
        return ResponseEntity.ok(dicomNodeService.updateDicomNode(id, request));
    }

    @DeleteMapping("/{id}")
    @RequiresPermission(PermissionCode.ENRUTAMIENTO_DELETE)
    @Operation(summary = "Eliminar nodo DICOM")
    public ResponseEntity<Void> deleteDicomNode(@PathVariable("id") UUID id) {
        dicomNodeService.deleteDicomNode(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/echo")
    @RequiresPermission(PermissionCode.ENRUTAMIENTO_VIEW)
    @Operation(summary = "Realizar DICOM Echo (C-ECHO)")
    public ResponseEntity<Map<String, Object>> performEcho(@PathVariable("id") UUID id) {
        boolean success = dicomNodeService.performEcho(id);
        return ResponseEntity.ok(Map.of(
                "success", success,
                "timestamp", System.currentTimeMillis(),
                "message", success ? "Echo successful" : "Echo failed"
        ));
    }

    @GetMapping("/active")
    @RequiresPermission(PermissionCode.ENRUTAMIENTO_VIEW)
    @Operation(summary = "Listar nodos DICOM activos")
    public ResponseEntity<List<DicomNodeResponse>> getActiveDicomNodes() {
        return ResponseEntity.ok(dicomNodeService.findActiveNodes());
    }

    @PostMapping("/{id}/toggle")
    @RequiresPermission(PermissionCode.ENRUTAMIENTO_EDIT)
    @Operation(summary = "Activar/Desactivar nodo DICOM")
    public ResponseEntity<DicomNodeResponse> toggleDicomNode(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(dicomNodeService.toggleActiveStatus(id));
    }

    @GetMapping("/stats")
    @RequiresPermission(PermissionCode.ENRUTAMIENTO_VIEW)
    @Operation(summary = "Obtener estadísticas de nodos DICOM")
    public ResponseEntity<Map<String, Object>> getDicomNodeStats() {
        List<DicomNodeResponse> allNodes = dicomNodeService.findAllDicomNodes();
        List<DicomNodeResponse> activeNodes = dicomNodeService.findActiveNodes();
        
        long scuNodes = allNodes.stream().filter(n -> "SCU".equals(n.getNodeType())).count();
        long scpNodes = allNodes.stream().filter(n -> "SCP".equals(n.getNodeType())).count();
        long bothNodes = allNodes.stream().filter(n -> "BOTH".equals(n.getNodeType())).count();
        
        return ResponseEntity.ok(Map.of(
                "totalNodes", allNodes.size(),
                "activeNodes", activeNodes.size(),
                "inactiveNodes", allNodes.size() - activeNodes.size(),
                "scuNodes", scuNodes,
                "scpNodes", scpNodes,
                "bothNodes", bothNodes,
                "timestamp", System.currentTimeMillis()
        ));
    }

    @PostMapping("/batch-echo")
    @RequiresPermission(PermissionCode.ENRUTAMIENTO_VIEW)
    @Operation(summary = "Realizar echo a todos los nodos activos")
    public ResponseEntity<Map<String, Object>> performBatchEcho() {
        Map<String, Boolean> results = dicomNodeService.performBatchEcho();
        
        long successCount = results.values().stream().mapToLong(success -> success ? 1 : 0).sum();
        long failureCount = results.size() - successCount;
        
        return ResponseEntity.ok(Map.of(
                "results", results,
                "totalTested", results.size(),
                "successCount", successCount,
                "failureCount", failureCount,
                "timestamp", System.currentTimeMillis()
        ));
    }
}