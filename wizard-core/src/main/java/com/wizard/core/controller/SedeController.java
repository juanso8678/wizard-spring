package com.wizard.core.controller;

import com.wizard.core.dto.SedeRequest;
import com.wizard.core.dto.SedeResponse;
import com.wizard.core.service.interfaces.SedeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sedes")
@RequiredArgsConstructor
@Tag(name = "Sedes", description = "Gestión de sedes vinculadas a una organización")
public class SedeController {

    private final SedeService sedeService;

    @PostMapping
    @Operation(summary = "Crear una nueva sede")
    public ResponseEntity<SedeResponse> create(@RequestBody SedeRequest request) {
        return ResponseEntity.ok(sedeService.create(request));
    }

    @GetMapping
    @Operation(summary = "Listar todas las sedes")
    public ResponseEntity<List<SedeResponse>> getAll() {
        return ResponseEntity.ok(sedeService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una sede por ID")
    public ResponseEntity<SedeResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(sedeService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una sede existente")
    public ResponseEntity<SedeResponse> update(@PathVariable UUID id, @RequestBody SedeRequest request) {
        return ResponseEntity.ok(sedeService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una sede")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        sedeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
