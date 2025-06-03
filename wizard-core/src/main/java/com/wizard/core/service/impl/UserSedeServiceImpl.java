package com.wizard.core.service.impl;

import com.wizard.core.config.TenantContext;
import com.wizard.core.dto.UserSedeRequest;
import com.wizard.core.dto.UserSedeResponse;
import com.wizard.core.entity.User;
import com.wizard.core.entity.Sede;
import com.wizard.core.entity.UserSede;
import com.wizard.core.exception.NotFoundException;
import com.wizard.core.exception.UnauthorizedException;
import com.wizard.core.exception.ValidationException;
import com.wizard.core.mapper.UserSedeMapper;
import com.wizard.core.repository.UserRepository;
import com.wizard.core.repository.SedeRepository;
import com.wizard.core.repository.UserSedeRepository;
import com.wizard.core.service.interfaces.UserSedeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSedeServiceImpl implements UserSedeService {

    private final UserSedeRepository userSedeRepository;
    private final UserRepository userRepository;
    private final SedeRepository sedeRepository;
    private final UserSedeMapper userSedeMapper;

    @Override
    public UserSedeResponse assignUserToSede(UserSedeRequest request) {
        UUID currentOrg = TenantContext.getOrganizationId();
        
        log.info("Assigning user {} to sede {}", request.getUserId(), request.getSedeId());
        
        // Verificar que el usuario existe
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        
        // Verificar que la sede existe
        Sede sede = sedeRepository.findById(request.getSedeId())
                .orElseThrow(() -> new NotFoundException("Sede no encontrada"));
        
        // Validar acceso por organización - usuario debe pertenecer a la organización actual
        if (currentOrg != null && !currentOrg.equals(user.getOrganizationId())) {
            throw new UnauthorizedException("No puede asignar usuarios de otra organización");
        }
        
        // Validar acceso por organización - sede debe pertenecer a la organización actual
        if (currentOrg != null && !currentOrg.equals(sede.getOrganization().getId())) {
            throw new UnauthorizedException("No puede asignar usuarios a sedes de otra organización");
        }
        
        // Verificar que no esté ya asignado
        if (userSedeRepository.existsByUserIdAndSedeId(request.getUserId(), request.getSedeId())) {
            throw new ValidationException("El usuario ya está asignado a esta sede");
        }
        
        // Crear la asignación
        UserSede userSede = userSedeMapper.toEntity(request, user, sede);
        UserSede saved = userSedeRepository.save(userSede);
        
        log.info("User {} successfully assigned to sede {}", user.getUsername(), sede.getName());
        return userSedeMapper.toResponse(saved);
    }

    @Override
    public List<UserSedeResponse> findAll() {
        UUID currentOrg = TenantContext.getOrganizationId();
        
        if (currentOrg != null) {
            // Solo asignaciones de la organización actual
            return userSedeRepository.findAll().stream()
                    .filter(userSede -> currentOrg.equals(userSede.getUser().getOrganizationId()) &&
                                       currentOrg.equals(userSede.getSede().getOrganization().getId()))
                    .map(userSedeMapper::toResponse)
                    .collect(Collectors.toList());
        } else {
            // Super admin - ve todas las asignaciones
            return userSedeRepository.findAll().stream()
                    .map(userSedeMapper::toResponse)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<UserSedeResponse> findByUserId(UUID userId) {
        UUID currentOrg = TenantContext.getOrganizationId();
        
        // Verificar que el usuario existe
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        
        // Validar acceso por organización
        if (currentOrg != null && !currentOrg.equals(user.getOrganizationId())) {
            throw new UnauthorizedException("No tiene acceso a este usuario");
        }
        
        return userSedeRepository.findByUserId(userId).stream()
                .filter(userSede -> currentOrg == null || 
                                   currentOrg.equals(userSede.getSede().getOrganization().getId()))
                .map(userSedeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserSedeResponse> findBySedeId(UUID sedeId) {
        UUID currentOrg = TenantContext.getOrganizationId();
        
        // Verificar que la sede existe
        Sede sede = sedeRepository.findById(sedeId)
                .orElseThrow(() -> new NotFoundException("Sede no encontrada"));
        
        // Validar acceso por organización
        if (currentOrg != null && !currentOrg.equals(sede.getOrganization().getId())) {
            throw new UnauthorizedException("No tiene acceso a esta sede");
        }
        
        return userSedeRepository.findBySedeId(sedeId).stream()
                .filter(userSede -> currentOrg == null || 
                                   currentOrg.equals(userSede.getUser().getOrganizationId()))
                .map(userSedeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void removeUserFromSede(UUID userId, UUID sedeId) {
        UUID currentOrg = TenantContext.getOrganizationId();
        
        log.info("Removing user {} from sede {}", userId, sedeId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        
        Sede sede = sedeRepository.findById(sedeId)
                .orElseThrow(() -> new NotFoundException("Sede no encontrada"));
        
        // Validar acceso por organización
        if (currentOrg != null && !currentOrg.equals(user.getOrganizationId())) {
            throw new UnauthorizedException("No puede modificar usuarios de otra organización");
        }
        
        if (currentOrg != null && !currentOrg.equals(sede.getOrganization().getId())) {
            throw new UnauthorizedException("No puede modificar asignaciones de sedes de otra organización");
        }
        
        UserSede userSede = userSedeRepository.findByUserAndSede(user, sede)
                .orElseThrow(() -> new NotFoundException("El usuario no está asignado a esta sede"));
        
        userSedeRepository.delete(userSede);
        log.info("User {} successfully removed from sede {}", user.getUsername(), sede.getName());
    }

    @Override
    public boolean isUserAssignedToSede(UUID userId, UUID sedeId) {
        UUID currentOrg = TenantContext.getOrganizationId();
        
        // Validar acceso a ambos recursos
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        
        Sede sede = sedeRepository.findById(sedeId)
                .orElseThrow(() -> new NotFoundException("Sede no encontrada"));
        
        if (currentOrg != null && !currentOrg.equals(user.getOrganizationId())) {
            throw new UnauthorizedException("No tiene acceso a este usuario");
        }
        
        if (currentOrg != null && !currentOrg.equals(sede.getOrganization().getId())) {
            throw new UnauthorizedException("No tiene acceso a esta sede");
        }
        
        return userSedeRepository.existsByUserIdAndSedeId(userId, sedeId);
    }


}