package com.wizard.core.service.impl;

import com.wizard.core.config.TenantContext;
import com.wizard.core.dto.*;
import com.wizard.core.entity.*;
import com.wizard.core.exception.*;
import com.wizard.core.mapper.UserRoleMapper;
import com.wizard.core.repository.*;
import com.wizard.core.service.interfaces.UserRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserRoleServiceImpl implements UserRoleService {

    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleMapper userRoleMapper;

    @Override
    public UserRoleResponse assignRole(UserRoleRequest request) {
        UUID currentOrg = TenantContext.getOrganizationId();
        
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new NotFoundException("Rol no encontrado"));
        
        // Validar acceso por organización
        if (currentOrg != null && !currentOrg.equals(user.getOrganizationId())) {
            throw new UnauthorizedException("No puede asignar roles a usuarios de otra organización");
        }
        
        // Verificar que no esté ya asignado
        if (userRoleRepository.existsByUserIdAndRoleId(request.getUserId(), request.getRoleId())) {
            throw new ValidationException("El usuario ya tiene asignado este rol");
        }
        
        UserRole userRole = userRoleMapper.toEntity(request, user, role);
        UserRole saved = userRoleRepository.save(userRole);
        
        log.info("Role {} assigned to user {}", role.getName(), user.getUsername());
        return userRoleMapper.toResponse(saved);
    }

    @Override
    public List<UserRoleResponse> findByUser(UUID userId) {
        UUID currentOrg = TenantContext.getOrganizationId();
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        
        // Validar acceso por organización
        if (currentOrg != null && !currentOrg.equals(user.getOrganizationId())) {
            throw new UnauthorizedException("No tiene acceso a este usuario");
        }
        
        return userRoleRepository.findByUserId(userId).stream()
                .map(userRoleMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserRoleResponse> findByRole(UUID roleId) {
        return userRoleRepository.findByRoleId(roleId).stream()
                .map(userRoleMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void removeRole(UUID userId, UUID roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new NotFoundException("Rol no encontrado"));
        
        UserRole userRole = userRoleRepository.findByUserAndRole(user, role)
                .orElseThrow(() -> new NotFoundException("El usuario no tiene asignado este rol"));
        
        userRoleRepository.delete(userRole);
        log.info("Role {} removed from user {}", role.getName(), user.getUsername());
    }

    @Override
    public List<String> getUserPermissions(UUID userId) {
        UUID currentOrg = TenantContext.getOrganizationId();
        return userRoleRepository.findUserPermissions(userId, currentOrg);
    }
}