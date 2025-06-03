package com.wizard.core.service.impl;

import com.wizard.core.config.TenantContext;
import com.wizard.core.dto.UserRequest;
import com.wizard.core.dto.UserResponse;
import com.wizard.core.entity.User;
import com.wizard.core.exception.NotFoundException;
import com.wizard.core.exception.UnauthorizedException;
import com.wizard.core.mapper.UserMapper;
import com.wizard.core.repository.UserRepository;
import com.wizard.core.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponse create(UserRequest request) {
        UUID currentOrg = TenantContext.getOrganizationId();
        
        // Si hay contexto de organización, asignar automáticamente
        if (currentOrg != null && request.getOrganizationId() == null) {
            request.setOrganizationId(currentOrg);
        }
        
        // Validar que no intente crear usuario en otra organización
        if (currentOrg != null && request.getOrganizationId() != null && 
            !currentOrg.equals(request.getOrganizationId())) {
            throw new UnauthorizedException("No puede crear usuarios en otra organización");
        }
        
        User user = userMapper.toEntity(request);
        User saved = userRepository.save(user);
        
        log.info("User created: {} for organization: {}", saved.getUsername(), saved.getOrganizationId());
        return userMapper.toResponse(saved);
    }

    @Override
    public UserResponse getById(UUID id) {
        UUID currentOrg = TenantContext.getOrganizationId();
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        
        // Validar acceso por organización
        if (currentOrg != null && !currentOrg.equals(user.getOrganizationId())) {
            throw new UnauthorizedException("No tiene acceso a este usuario");
        }
        
        return userMapper.toResponse(user);
    }

    @Override
    public List<UserResponse> getAll() {
        UUID currentOrg = TenantContext.getOrganizationId();
        
        if (currentOrg != null) {
            // Solo usuarios de la organización actual
            log.debug("Filtering users by organization: {}", currentOrg);
            return userRepository.findByOrganizationId(currentOrg)
                    .stream()
                    .map(userMapper::toResponse)
                    .toList();
        } else {
            // Super admin sin organización - ve todos los usuarios
            log.debug("No organization context - returning all users (super admin)");
            return userRepository.findAll()
                    .stream()
                    .map(userMapper::toResponse)
                    .toList();
        }
    }

    @Override
    public void delete(UUID id) {
        UUID currentOrg = TenantContext.getOrganizationId();
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
        
        // Validar acceso por organización
        if (currentOrg != null && !currentOrg.equals(user.getOrganizationId())) {
            throw new UnauthorizedException("No puede eliminar usuarios de otra organización");
        }
        
        userRepository.deleteById(id);
        log.info("User deleted: {} from organization: {}", user.getUsername(), user.getOrganizationId());
    }
}