package com.wizard.core.controller;

import com.wizard.core.dto.UserSedeRequest;
import com.wizard.core.dto.UserSedeResponse;
import com.wizard.core.service.interfaces.UserSedeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/user-sedes")
@RequiredArgsConstructor
@Tag(name = "Usuario-Sedes", description = "Gestión de asignaciones de usuarios a sedes")
public class UserSedeController {

    private final UserSedeService userSedeService;

    @PostMapping
    @Operation(summary = "Asignar usuario a una sede")
    public ResponseEntity<UserSedeResponse> assignUserToSede(@RequestBody UserSedeRequest request) {
        return ResponseEntity.ok(userSedeService.assignUserToSede(request));
    }

    @GetMapping
    @Operation(summary = "Listar todas las asignaciones usuario-sede")
    public ResponseEntity<List<UserSedeResponse>> getAll() {
        return ResponseEntity.ok(userSedeService.findAll());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Obtener sedes asignadas a un usuario")
    public ResponseEntity<List<UserSedeResponse>> getSedesByUser(@PathVariable("userId") UUID userId) {
        return ResponseEntity.ok(userSedeService.findByUserId(userId));
    }

    @GetMapping("/sede/{sedeId}")
    @Operation(summary = "Obtener usuarios asignados a una sede")
    public ResponseEntity<List<UserSedeResponse>> getUsersBySede(@PathVariable("sedeId") UUID sedeId) {
        return ResponseEntity.ok(userSedeService.findBySedeId(sedeId));
    }

    @GetMapping("/check/{userId}/{sedeId}")
    @Operation(summary = "Verificar si un usuario está asignado a una sede")
    public ResponseEntity<Map<String, Boolean>> checkAssignment(
            @PathVariable("userId") UUID userId, 
            @PathVariable("sedeId") UUID sedeId) {
        boolean isAssigned = userSedeService.isUserAssignedToSede(userId, sedeId);
        return ResponseEntity.ok(Map.of("isAssigned", isAssigned));
    }

    @DeleteMapping("/{userId}/{sedeId}")
    @Operation(summary = "Remover usuario de una sede")
    public ResponseEntity<Void> removeUserFromSede(
            @PathVariable("userId") UUID userId, 
            @PathVariable("sedeId") UUID sedeId) {
        userSedeService.removeUserFromSede(userId, sedeId);
        return ResponseEntity.noContent().build();
    }
}