package com.wizard.core.modules.pacs.controller;

import com.wizard.core.enums.PermissionCode;
import com.wizard.core.modules.pacs.dto.*;
import com.wizard.core.modules.pacs.service.interfaces.StudyService;
import com.wizard.core.security.annotation.RequiresPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;  // ✅ IMPORT AGREGADO
import java.util.UUID;

@RestController
@RequestMapping("/api/pacs/studies")
@RequiredArgsConstructor
@Tag(name = "PACS - Estudios", description = "Gestión de estudios DICOM")
public class StudyController {

    private final StudyService studyService;

    @PostMapping
    @RequiresPermission(PermissionCode.PACS_IMPORT)
    @Operation(summary = "Crear nuevo estudio")
    public ResponseEntity<StudyResponse> createStudy(@RequestBody StudyRequest request) {
        return ResponseEntity.ok(studyService.createStudy(request));
    }

    @GetMapping
    @RequiresPermission(PermissionCode.PACS_VIEW)
    @Operation(summary = "Listar todos los estudios")
    public ResponseEntity<List<StudyResponse>> getAllStudies() {
        return ResponseEntity.ok(studyService.findAllStudies());
    }

    @GetMapping("/{id}")
    @RequiresPermission(PermissionCode.PACS_VIEW)
    @Operation(summary = "Obtener estudio por ID")
    public ResponseEntity<StudyResponse> getStudyById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(studyService.findStudyById(id));
    }

    @GetMapping("/uid/{studyInstanceUID}")
    @RequiresPermission(PermissionCode.PACS_VIEW)
    @Operation(summary = "Obtener estudio por Instance UID")
    public ResponseEntity<StudyResponse> getStudyByUID(@PathVariable("studyInstanceUID") String studyInstanceUID) {
        return ResponseEntity.ok(studyService.findStudyByInstanceUID(studyInstanceUID));
    }

    @GetMapping("/search")
    @RequiresPermission(PermissionCode.PACS_VIEW)
    @Operation(summary = "Buscar estudios con filtros")
    public ResponseEntity<List<StudyResponse>> searchStudies(
            @RequestParam(required = false) String patientId,
            @RequestParam(required = false) String patientName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate studyDate,
            @RequestParam(required = false) String modality,
            @RequestParam(required = false) String accessionNumber) {
        
        return ResponseEntity.ok(studyService.searchStudies(
                patientId, patientName, studyDate, modality, accessionNumber));
    }

    @PutMapping("/{id}")
    @RequiresPermission(PermissionCode.PACS_EDIT)
    @Operation(summary = "Actualizar estudio")
    public ResponseEntity<StudyResponse> updateStudy(
            @PathVariable("id") UUID id, 
            @RequestBody StudyRequest request) {
        return ResponseEntity.ok(studyService.updateStudy(id, request));
    }

    @DeleteMapping("/{id}")
    @RequiresPermission(PermissionCode.PACS_DELETE)
    @Operation(summary = "Eliminar estudio")
    public ResponseEntity<Void> deleteStudy(@PathVariable("id") UUID id) {
        studyService.deleteStudy(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/series")
    @RequiresPermission(PermissionCode.PACS_VIEW)
    @Operation(summary = "Obtener series de un estudio")
    public ResponseEntity<List<SeriesResponse>> getSeriesByStudy(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(studyService.getSeriesByStudy(id));
    }

    @GetMapping("/stats")
    @RequiresPermission(PermissionCode.PACS_VIEW)
    @Operation(summary = "Obtener estadísticas de estudios")
    public ResponseEntity<Map<String, Object>> getStudyStats() {
        Long totalStudies = studyService.getStudyCount();
        return ResponseEntity.ok(Map.of(
                "totalStudies", totalStudies,
                "timestamp", System.currentTimeMillis()
        ));
    }

    @GetMapping("/patient/{patientId}")
    @RequiresPermission(PermissionCode.PACS_VIEW)
    @Operation(summary = "Obtener estudios por ID de paciente")
    public ResponseEntity<List<StudyResponse>> getStudiesByPatient(@PathVariable("patientId") String patientId) {
        return ResponseEntity.ok(studyService.findStudiesByPatientId(patientId));
    }

    @PostMapping("/{id}/reprocess")
    @RequiresPermission(PermissionCode.PACS_EDIT)
    @Operation(summary = "Reprocesar estudio")
    public ResponseEntity<StudyResponse> reprocessStudy(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(studyService.reprocessStudy(id));
    }
}