package com.wizard.core.modules.pacs.controller;

import com.wizard.core.enums.PermissionCode;
import com.wizard.core.modules.pacs.dto.ViewerRequest;
import com.wizard.core.modules.pacs.dto.ViewerResponse;
import com.wizard.core.modules.pacs.service.interfaces.ViewerService;
import com.wizard.core.security.annotation.RequiresPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;  // ✅ IMPORT AGREGADO

@RestController
@RequestMapping("/api/pacs/viewer")
@RequiredArgsConstructor
@Tag(name = "PACS - Visor", description = "Visor web de imágenes DICOM")
public class ViewerController {

    private final ViewerService viewerService;

    @PostMapping("/launch")
    @RequiresPermission(PermissionCode.VISOR_WEB_VIEW)
    @Operation(summary = "Lanzar visor web")
    public ResponseEntity<ViewerResponse> launchViewer(@RequestBody ViewerRequest request) {
        return ResponseEntity.ok(viewerService.generateViewerUrl(request));
    }

    @GetMapping("/launch/{studyInstanceUID}")
    @RequiresPermission(PermissionCode.VISOR_WEB_VIEW)
    @Operation(summary = "Lanzar visor para estudio específico")
    public ResponseEntity<ViewerResponse> launchViewerForStudy(
            @PathVariable("studyInstanceUID") String studyInstanceUID) {
        return ResponseEntity.ok(viewerService.generateViewerUrlForStudy(studyInstanceUID));
    }

    @PostMapping("/validate")
    @Operation(summary = "Validar token de sesión del visor")
    public ResponseEntity<Map<String, Boolean>> validateViewerSession(@RequestBody Map<String, String> request) {
        String sessionToken = request.get("sessionToken");
        boolean isValid = viewerService.validateViewerAccess(sessionToken);
        
        return ResponseEntity.ok(Map.of("valid", isValid));
    }
}