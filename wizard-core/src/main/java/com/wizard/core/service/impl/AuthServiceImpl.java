package com.wizard.core.service.impl;

import com.wizard.core.dto.auth.LoginRequest;
import com.wizard.core.dto.auth.JwtResponse;
import com.wizard.core.entity.User;
import com.wizard.core.exception.UnauthorizedException;
import com.wizard.core.repository.UserRepository;
import com.wizard.core.security.JwtService;
import com.wizard.core.service.interfaces.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public JwtResponse login(LoginRequest request) {
        log.info("Login attempt for user: {}", request.getUsernameOrEmail());
        
        try {
            // 1. Buscar usuario por username o email
            User user = userRepository.findByUsernameOrEmail(
                request.getUsernameOrEmail(), 
                request.getUsernameOrEmail()
            ).orElseThrow(() -> {
                log.warn("Login failed: User not found - {}", request.getUsernameOrEmail());
                return new UnauthorizedException("Credenciales inválidas");
            });

            
            // 2. Verificar contraseña
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                log.warn("Login failed: Invalid password for user - {}", user.getUsername());
                throw new UnauthorizedException("Credenciales inválidas"); // ← AHORA CON THROW
            }
            
            // 3. Verificar que el usuario esté activo
            if (!user.isActivo()) {
                log.warn("Login failed: User inactive - {}", user.getUsername());
                throw new UnauthorizedException("Usuario inactivo");
            }
            
            // 4. Generar JWT token
            String token = jwtService.generateToken(
            Map.of(
                "userId", user.getId().toString(),
                "role", user.getRole(),
                "organizationId", user.getOrganizationId() != null ? 
                    user.getOrganizationId().toString() : "no-org" // ✅ AGREGAR ESTA LÍNEA
            ), 
            user.getUsername()
        );
            
            log.info("Login successful for user: {}", user.getUsername());
            return new JwtResponse(token);


            
        } catch (UnauthorizedException e) {
            // Re-lanzar excepciones de autorización
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during login for user: {}", request.getUsernameOrEmail(), e);
            throw new UnauthorizedException("Error interno de autenticación");
        }
    }
}