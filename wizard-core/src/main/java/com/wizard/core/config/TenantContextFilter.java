package com.wizard.core.config;

import com.wizard.core.security.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

/**
 * Filtro para establecer automáticamente el contexto de tenant/organización
 */
@Slf4j
@Component
@Order(1) // Ejecutar antes que otros filtros
public class TenantContextFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        
        try {
            // Establecer contexto antes de procesar la request
            setupTenantContext(httpRequest);
            
            // Continuar con la cadena de filtros
            chain.doFilter(request, response);
            
        } finally {
            // Siempre limpiar el contexto al final
            TenantContext.clear();
        }
    }
    
    /**
     * Establece el contexto de tenant basado en la request
     */
    private void setupTenantContext(HttpServletRequest request) {
        // 1. Intentar obtener organization ID del header
        String orgHeader = request.getHeader("X-Organization-Id");
        if (orgHeader != null) {
            try {
                UUID orgId = UUID.fromString(orgHeader);
                TenantContext.setOrganizationId(orgId);
                log.debug("📄 [FILTER] Set org from header: {}", orgId);
                return;
            } catch (IllegalArgumentException e) {
                log.warn("📄 [FILTER] Invalid org ID in header: {}", orgHeader);
            }
        }
        
        // 2. Intentar extraer del path (/api/org/{orgId}/...)
        String path = request.getRequestURI();
        UUID orgFromPath = extractOrgFromPath(path);
        if (orgFromPath != null) {
            TenantContext.setOrganizationId(orgFromPath);
            log.debug("📄 [FILTER] Set org from path: {}", orgFromPath);
            return;
        }
        
        // 3. Como último recurso, obtener del usuario autenticado
        UUID userId = SecurityUtils.getCurrentUserId();
        if (userId != null) {
            TenantContext.setCurrentUser(userId);
            // Aquí podrías buscar la organización del usuario en BD
            // UUID userOrg = userService.getUserPrimaryOrganization(userId);
            // if (userOrg != null) TenantContext.setOrganizationId(userOrg);
            log.debug("📄 [FILTER] Set user: {}", userId);
        }
    }
    
    /**
     * Extrae organization ID del path de la URL
     */
    private UUID extractOrgFromPath(String path) {
        if (path == null || !path.contains("/org/")) {
            return null;
        }
        
        String[] parts = path.split("/");
        for (int i = 0; i < parts.length - 1; i++) {
            if ("org".equals(parts[i]) && i + 1 < parts.length) {
                try {
                    return UUID.fromString(parts[i + 1]);
                } catch (IllegalArgumentException e) {
                    log.warn("📄 [FILTER] Invalid org ID in path: {}", parts[i + 1]);
                    return null;
                }
            }
        }
        return null;
    }
}