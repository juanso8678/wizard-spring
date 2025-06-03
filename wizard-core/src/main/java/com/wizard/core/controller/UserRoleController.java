package com.wizard.core.controller;

import com.wizard.core.dto.*;
import com.wizard.core.service.interfaces.UserRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/user-roles")
@RequiredArgsConstructor
@Tag(name = "Usuario-Roles", description = "Gestión de asignación de roles a usuarios")
public class UserRoleController {

    private final UserRoleService userRoleService;

    @PostMapping
    @Operation(summary = "Asignar rol a usuario")
    public ResponseEntity<UserRoleResponse> assignRole(@RequestBody UserRoleRequest request) {
        return ResponseEntity.ok(userRoleService.assignRole(request));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Obtener roles de un usuario")
    public ResponseEntity<List<UserRoleResponse>> getRolesByUser(@PathVariable("userId") UUID userId) {
        return ResponseEntity.ok(userRoleService.findByUser(userId));
    }

    @GetMapping("/role/{roleId}")
    @Operation(summary = "Obtener usuarios con un rol específico")
    public ResponseEntity<List<UserRoleResponse>> getUsersByRole(@PathVariable("roleId") UUID roleId) {
        return ResponseEntity.ok(userRoleService.findByRole(roleId));
    }

    @DeleteMapping("/{userId}/{roleId}")
    @Operation(summary = "Remover rol de usuario")
    public ResponseEntity<Void> removeRole(
            @PathVariable("userId") UUID userId, 
            @PathVariable("roleId") UUID roleId) {
        userRoleService.removeRole(userId, roleId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}/permissions")
    @Operation(summary = "Obtener permisos de un usuario")
    public ResponseEntity<Map<String, List<String>>> getUserPermissions(@PathVariable("userId") UUID userId) {
        List<String> permissions = userRoleService.getUserPermissions(userId);
        return ResponseEntity.ok(Map.of("permissions", permissions));
    }
}