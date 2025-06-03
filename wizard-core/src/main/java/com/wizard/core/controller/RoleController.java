package com.wizard.core.controller;

import com.wizard.core.dto.*;
import com.wizard.core.service.interfaces.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Tag(name = "Roles", description = "Gestión de roles del sistema")
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    @Operation(summary = "Crear un nuevo rol")
    public ResponseEntity<RoleResponse> create(@RequestBody RoleRequest request) {
        return ResponseEntity.ok(roleService.create(request));
    }

    @GetMapping
    @Operation(summary = "Listar todos los roles")
    public ResponseEntity<List<RoleResponse>> getAll() {
        return ResponseEntity.ok(roleService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener rol por ID")
    public ResponseEntity<RoleResponse> getById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(roleService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un rol")
    public ResponseEntity<RoleResponse> update(@PathVariable("id") UUID id, @RequestBody RoleRequest request) {
        return ResponseEntity.ok(roleService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un rol")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id) {
        roleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/permissions")
    @Operation(summary = "Asignar permisos a un rol")
    public ResponseEntity<RoleResponse> assignPermissions(
            @PathVariable("id") UUID id, 
            @RequestBody List<UUID> permissionIds) {
        return ResponseEntity.ok(roleService.assignPermissions(id, permissionIds));
    }

    @GetMapping("/organization/{orgId}")
    @Operation(summary = "Obtener roles por organización")
    public ResponseEntity<List<RoleResponse>> getByOrganization(@PathVariable("orgId") UUID orgId) {
        return ResponseEntity.ok(roleService.findByOrganization(orgId));
    }
}
