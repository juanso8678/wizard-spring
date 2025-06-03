package com.wizard.core.security.interceptor;

import com.wizard.core.config.TenantContext;
import com.wizard.core.enums.PermissionCode;
import com.wizard.core.security.annotation.RequiresPermission;
import com.wizard.core.security.service.PermissionService;
import com.wizard.core.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PermissionInterceptor implements HandlerInterceptor {

    private final PermissionService permissionService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, 
                           Object handler) throws Exception {
        
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RequiresPermission annotation = handlerMethod.getMethodAnnotation(RequiresPermission.class);
        
        if (annotation == null) {
            annotation = handlerMethod.getBeanType().getAnnotation(RequiresPermission.class);
        }
        
        if (annotation == null) {
            return true; // No requiere permisos específicos
        }
        
        // Obtener usuario actual
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            log.warn("🚫 [SECURITY] No authenticated user for protected endpoint: {}", 
                    request.getRequestURI());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Authentication required\"}");
            return false;
        }
        
        // ✅ EXTRAER ORGANIZATION ID DEL REQUEST HEADER O PATH
        extractAndSetOrganizationContext(request);
        
        // Verificar bypass para super admin
        if (annotation.allowSuperAdminBypass() && 
            permissionService.isSuperAdmin(currentUserId)) {
            log.debug("✅ [SECURITY] Super admin bypass for user: {} endpoint: {}", 
                     currentUserId, request.getRequestURI());
            return true;
        }
        
        // Preparar lista de permisos a verificar
        List<PermissionCode> requiredPermissions = Arrays.asList(annotation.additional());
        requiredPermissions.add(0, annotation.value());
        
        // Verificar permisos
        boolean hasPermission;
        if (annotation.requireAll()) {
            hasPermission = permissionService.hasAllPermissions(currentUserId, requiredPermissions);
        } else {
            hasPermission = permissionService.hasAnyPermission(currentUserId, requiredPermissions);
        }
        
        if (!hasPermission) {
            log.warn("🚫 [SECURITY] Access denied for user: {} to endpoint: {} - Missing permissions: {}", 
                    currentUserId, request.getRequestURI(), requiredPermissions);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"" + annotation.message() + "\"}");
            return false;
        }
        
        log.debug("✅ [SECURITY] Access granted for user: {} to endpoint: {}", 
                 currentUserId, request.getRequestURI());
        
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                              Object handler, Exception ex) throws Exception {
        // Limpiar contexto de tenant
        TenantContext.clear();
    }
    
    /**
     * Extrae el organization ID del request y lo establece en el contexto
     */
    private void extractAndSetOrganizationContext(HttpServletRequest request) {
        // ✅ OPCIÓN 1: Desde header
        String orgHeader = request.getHeader("X-Organization-Id");
        if (orgHeader != null) {
            try {
                UUID orgId = UUID.fromString(orgHeader);
                TenantContext.setOrganizationId(orgId);
                TenantContext.setCurrentTenant(orgId.toString());
                return;
            } catch (IllegalArgumentException e) {
                log.warn("Invalid organization ID in header: {}", orgHeader);
            }
        }
        
        // ✅ OPCIÓN 2: Desde path parameter (ej: /api/org/{orgId}/...)
        String path = request.getRequestURI();
        if (path.contains("/org/")) {
            String[] parts = path.split("/");
            for (int i = 0; i < parts.length - 1; i++) {
                if ("org".equals(parts[i]) && i + 1 < parts.length) {
                    try {
                        UUID orgId = UUID.fromString(parts[i + 1]);
                        TenantContext.setOrganizationId(orgId);
                        TenantContext.setCurrentTenant(orgId.toString());
                        return;
                    } catch (IllegalArgumentException e) {
                        log.warn("Invalid organization ID in path: {}", parts[i + 1]);
                    }
                }
            }
        }
        
        // ✅ OPCIÓN 3: Obtener de la sesión del usuario (implementar según tu lógica)
        // UUID userOrgId = getUserOrganizationFromSession();
        // if (userOrgId != null) {
        //     TenantContext.setOrganizationId(userOrgId);
        //     TenantContext.setCurrentTenant(userOrgId.toString());
        // }
    }
}