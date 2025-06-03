package com.wizard.core.controller;

import com.wizard.core.dto.PermissionResponse;
import com.wizard.core.dto.ModuleResponse;
import com.wizard.core.repository.PermissionRepository;
import com.wizard.core.repository.ModuleRepository;
import com.wizard.core.mapper.PermissionMapper;
import com.wizard.core.mapper.ModuleMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
@Tag(name = "Permisos", description = "Consulta de permisos y módulos del sistema")
public class PermissionController {

    private final PermissionRepository permissionRepository;
    private final ModuleRepository moduleRepository;
    private final PermissionMapper permissionMapper;
    private final ModuleMapper moduleMapper;

    @GetMapping
    @Operation(summary = "Listar todos los permisos")
    public ResponseEntity<List<PermissionResponse>> getAllPermissions() {
        return ResponseEntity.ok(
            permissionRepository.findByActiveTrue().stream()
                .map(permissionMapper::toResponse)
                .collect(Collectors.toList())
        );
    }

    @GetMapping("/modules")
    @Operation(summary = "Listar todos los módulos")
    public ResponseEntity<List<ModuleResponse>> getAllModules() {
        return ResponseEntity.ok(
            moduleRepository.findByActiveTrue().stream()
                .map(moduleMapper::toResponse)
                .collect(Collectors.toList())
        );
    }

    @GetMapping("/modules/{moduleId}")
    @Operation(summary = "Obtener permisos de un módulo específico")
    public ResponseEntity<List<PermissionResponse>> getPermissionsByModule(@PathVariable("moduleId") java.util.UUID moduleId) {
        return ResponseEntity.ok(
            permissionRepository.findByModuleId(moduleId).stream()
                .map(permissionMapper::toResponse)
                .collect(Collectors.toList())
        );
    }
}