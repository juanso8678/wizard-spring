package com.wizard.core.service.impl;

import com.wizard.core.config.TenantContext;
import com.wizard.core.dto.*;
import com.wizard.core.entity.*;
import com.wizard.core.exception.*;
import com.wizard.core.mapper.RoleMapper;
import com.wizard.core.repository.*;
import com.wizard.core.service.interfaces.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RoleMapper roleMapper;

    @Override
    public RoleResponse create(RoleRequest request) {
        UUID currentOrg = TenantContext.getOrganizationId();
        
        // Si hay contexto de organización, debe coincidir
        if (currentOrg != null && request.getOrganizationId() != null && 
            !currentOrg.equals(request.getOrganizationId())) {
            throw new UnauthorizedException("No puede crear roles en otra organización");
        }
        
        // Si hay contexto pero no se especifica org en request, usar contexto
        if (currentOrg != null && request.getOrganizationId() == null) {
            request.setOrganizationId(currentOrg);
        }
        
        // Verificar que no existe un rol con el mismo nombre
        if (roleRepository.existsByName(request.getName())) {
            throw new ValidationException("Ya existe un rol con ese nombre");
        }
        
        Role role = roleMapper.toEntity(request);
        
        // Asignar permisos si se especificaron
        if (request.getPermissionIds() != null && !request.getPermissionIds().isEmpty()) {
            List<Permission> permissions = permissionRepository.findAllById(request.getPermissionIds());
            role.setPermissions(permissions);
        }
        
        Role saved = roleRepository.save(role);
        log.info("Role created: {} for organization: {}", saved.getName(), saved.getOrganizationId());
        
        return roleMapper.toResponse(saved);
    }

    @Override
    public List<RoleResponse> findAll() {
        UUID currentOrg = TenantContext.getOrganizationId();
        
        if (currentOrg != null) {
            // Solo roles de la organización actual + roles globales
            List<Role> orgRoles = roleRepository.findByOrganizationId(currentOrg);
            List<Role> globalRoles = roleRepository.findByOrganizationIdIsNull();
            
            List<Role> allRoles = List.of();
            allRoles.addAll(orgRoles);
            allRoles.addAll(globalRoles);
            
            return allRoles.stream()
                    .map(roleMapper::toResponse)
                    .collect(Collectors.toList());
        } else {
            // Super admin - ve todos los roles
            return roleRepository.findAll().stream()
                    .map(roleMapper::toResponse)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public RoleResponse findById(UUID id) {
        UUID currentOrg = TenantContext.getOrganizationId();
        
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Rol no encontrado"));
        
        // Validar acceso por organización
        if (currentOrg != null && role.getOrganizationId() != null && 
            !currentOrg.equals(role.getOrganizationId())) {
            throw new UnauthorizedException("No tiene acceso a este rol");
        }
        
        return roleMapper.toResponse(role);
    }

    @Override
    public RoleResponse update(UUID id, RoleRequest request) {
        UUID currentOrg = TenantContext.getOrganizationId();
        
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Rol no encontrado"));
        
        // Validar acceso por organización
        if (currentOrg != null && role.getOrganizationId() != null && 
            !currentOrg.equals(role.getOrganizationId())) {
            throw new UnauthorizedException("No puede modificar roles de otra organización");
        }
        
        role.setDisplayName(request.getDisplayName());
        role.setDescription(request.getDescription());
        
        // Actualizar permisos si se especificaron
        if (request.getPermissionIds() != null) {
            List<Permission> permissions = permissionRepository.findAllById(request.getPermissionIds());
            role.setPermissions(permissions);
        }
        
        Role saved = roleRepository.save(role);
        log.info("Role updated: {}", saved.getName());
        
        return roleMapper.toResponse(saved);
    }

    @Override
    public void delete(UUID id) {
        UUID currentOrg = TenantContext.getOrganizationId();
        
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Rol no encontrado"));
        
        // Validar acceso por organización
        if (currentOrg != null && role.getOrganizationId() != null && 
            !currentOrg.equals(role.getOrganizationId())) {
            throw new UnauthorizedException("No puede eliminar roles de otra organización");
        }
        
        roleRepository.deleteById(id);
        log.info("Role deleted: {}", role.getName());
    }

    @Override
    public List<RoleResponse> findByOrganization(UUID organizationId) {
        UUID currentOrg = TenantContext.getOrganizationId();
        
        // Validar acceso
        if (currentOrg != null && !currentOrg.equals(organizationId)) {
            throw new UnauthorizedException("No tiene acceso a roles de otra organización");
        }
        
        return roleRepository.findByOrganizationId(organizationId).stream()
                .map(roleMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RoleResponse assignPermissions(UUID roleId, List<UUID> permissionIds) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new NotFoundException("Rol no encontrado"));
        
        List<Permission> permissions = permissionRepository.findAllById(permissionIds);
        role.setPermissions(permissions);
        
        Role saved = roleRepository.save(role);
        log.info("Permissions assigned to role: {}", saved.getName());
        
        return roleMapper.toResponse(saved);
    }
}