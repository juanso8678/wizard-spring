package com.wizard.core.security.service;

import com.wizard.core.config.TenantContext;
import com.wizard.core.enums.PermissionCode;
import com.wizard.core.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionService {

    private final UserRoleRepository userRoleRepository;

    /**
     * Verifica si un usuario tiene un permiso específico
     */
    public boolean hasPermission(UUID userId, PermissionCode permission) {
        try {
            UUID organizationId = TenantContext.getOrganizationId();
            log.debug("Verificando permiso {} para usuario {} en org {}", 
                     permission, userId, organizationId);
            
            List<String> userPermissions = getUserPermissionCodes(userId, organizationId);
            boolean hasPermission = userPermissions.contains(permission.name());
            
            log.debug("Usuario {} {} tiene permiso {}", 
                     userId, hasPermission ? "SÍ" : "NO", permission);
            
            return hasPermission;
        } catch (Exception e) {
            log.error("Error verificando permiso {} para usuario {}: {}", 
                     permission, userId, e.getMessage());
            return false;
        }
    }

    /**
     * Verifica si un usuario tiene todos los permisos especificados
     */
    public boolean hasAllPermissions(UUID userId, List<PermissionCode> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return true;
        }
        
        try {
            UUID organizationId = TenantContext.getOrganizationId();
            List<String> userPermissions = getUserPermissionCodes(userId, organizationId);
            
            List<String> requiredPermissions = permissions.stream()
                    .map(PermissionCode::name)
                    .collect(Collectors.toList());
            
            boolean hasAll = userPermissions.containsAll(requiredPermissions);
            
            log.debug("Usuario {} {} tiene todos los permisos solicitados", 
                     userId, hasAll ? "SÍ" : "NO");
            
            return hasAll;
        } catch (Exception e) {
            log.error("Error verificando permisos múltiples para usuario {}: {}", 
                     userId, e.getMessage());
            return false;
        }
    }

    /**
     * Verifica si un usuario tiene al menos uno de los permisos especificados
     */
    public boolean hasAnyPermission(UUID userId, List<PermissionCode> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return false;
        }
        
        try {
            UUID organizationId = TenantContext.getOrganizationId();
            List<String> userPermissions = getUserPermissionCodes(userId, organizationId);
            
            boolean hasAny = permissions.stream()
                    .map(PermissionCode::name)
                    .anyMatch(userPermissions::contains);
            
            log.debug("Usuario {} {} tiene alguno de los permisos solicitados", 
                     userId, hasAny ? "SÍ" : "NO");
            
            return hasAny;
        } catch (Exception e) {
            log.error("Error verificando permisos alternativos para usuario {}: {}", 
                     userId, e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene todos los permisos de un usuario como códigos string
     */
    public List<String> getUserPermissionCodes(UUID userId, UUID organizationId) {
        try {
            log.debug("Obteniendo permisos para usuario {} en org {}", userId, organizationId);
            
            // ✅ USANDO TU MÉTODO EXISTENTE
            List<String> permissions = userRoleRepository.findUserPermissions(userId, organizationId);
            
            log.debug("Usuario {} tiene {} permisos", userId, permissions.size());
            
            return permissions;
        } catch (Exception e) {
            log.error("Error obteniendo permisos para usuario {}: {}", userId, e.getMessage());
            return List.of();
        }
    }

    /**
     * Obtiene todos los permisos de un usuario como PermissionCode
     */
    public List<PermissionCode> getUserPermissions(UUID userId) {
        UUID organizationId = TenantContext.getOrganizationId();
        List<String> permissionCodes = getUserPermissionCodes(userId, organizationId);
        
        return permissionCodes.stream()
                .map(code -> {
                    try {
                        return PermissionCode.valueOf(code);
                    } catch (IllegalArgumentException e) {
                        log.warn("Código de permiso desconocido: {}", code);
                        return null;
                    }
                })
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todos los permisos únicos de un usuario
     */
    public Set<PermissionCode> getUniqueUserPermissions(UUID userId) {
        return getUserPermissions(userId).stream()
               .collect(Collectors.toSet());
    }

    /**
     * Verifica si un usuario tiene permisos de administrador
     */
    public boolean isAdmin(UUID userId) {
        return hasPermission(userId, PermissionCode.ADMIN_ACCESS);
    }

    /**
     * Verifica si un usuario tiene permisos de super administrador
     */
    public boolean isSuperAdmin(UUID userId) {
        return hasPermission(userId, PermissionCode.SUPER_ADMIN_ACCESS);
    }

    /**
     * Verifica si un usuario puede acceder a un recurso específico
     */
    public boolean canAccessResource(UUID userId, PermissionCode resourcePermission) {
        // Los super admins pueden acceder a cualquier recurso
        if (isSuperAdmin(userId)) {
            return true;
        }
        
        // Verificar permiso específico
        return hasPermission(userId, resourcePermission);
    }
}