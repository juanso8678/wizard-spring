package com.wizard.core.controller;

import com.wizard.core.dto.auth.LoginRequest;
import com.wizard.core.dto.auth.JwtResponse;
import com.wizard.core.entity.User;
import com.wizard.core.repository.UserRepository;
import com.wizard.core.security.JwtService;
import com.wizard.core.service.interfaces.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

     @PostMapping("/login")
    public JwtResponse login(@RequestBody LoginRequest request) {
        // ✅ Usar lógica manual que funciona - SIN AuthenticationManager
        try {
            System.out.println("🔍 [LOGIN] Login attempt for: " + request.getUsernameOrEmail());
            
            // Buscar usuario
            User user = userRepository.findByUsernameOrEmail(
                request.getUsernameOrEmail(), 
                request.getUsernameOrEmail()
            ).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            // Verificar contraseña
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new RuntimeException("Contraseña incorrecta");
            }
            
            // Verificar si está activo
            if (!user.isActivo()) {
                throw new RuntimeException("Usuario inactivo");
            }
            
            // Generar token
            String token = jwtService.generateToken(Map.of(), user.getUsername());
            
            System.out.println("✅ [LOGIN] Login successful for: " + user.getUsername());
            return new JwtResponse(token);
            
        } catch (Exception e) {
            System.out.println("❌ [LOGIN] Login failed: " + e.getMessage());
            throw e;
        }
    }

    @PostMapping("/login-manual")
    public ResponseEntity<?> loginManual(@RequestBody LoginRequest request) {
        try {
            System.out.println("🔍 Manual login for: " + request.getUsernameOrEmail());
            
            // Paso 1: Buscar usuario
            User user = userRepository.findByUsernameOrEmail(
                request.getUsernameOrEmail(), 
                request.getUsernameOrEmail()
            ).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            System.out.println("✅ Usuario encontrado: " + user.getUsername());
            
            // Paso 2: Verificar contraseña
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                return ResponseEntity.status(401).body(Map.of("error", "Contraseña incorrecta"));
            }
            
            System.out.println("✅ Contraseña correcta");
            
            // Paso 3: Verificar si está activo
            if (!user.isActivo()) {
                return ResponseEntity.status(401).body(Map.of("error", "Usuario inactivo"));
            }
            
            System.out.println("✅ Usuario activo");
            
            // Paso 4: Generar token
            String token = jwtService.generateToken(Map.of(), user.getUsername());
            System.out.println("✅ Token generado");
            
            return ResponseEntity.ok(new JwtResponse(token));
            
        } catch (Exception e) {
            System.out.println("❌ Error en login manual: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}