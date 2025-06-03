package com.wizard.core.config;

import com.wizard.core.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * Interceptor que extrae el organizationId del JWT y lo establece en TenantContext
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TenantInterceptor implements HandlerInterceptor {
    
    private final JwtService jwtService;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        
        // Skip para endpoints públicos
        if (isPublicEndpoint(request.getRequestURI())) {
            return true;
        }
        
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String token = authHeader.substring(7);
                
                // Extraer organizationId del JWT
                String orgId = jwtService.extractClaim(token, claims -> 
                    claims.get("organizationId", String.class));
                
                if (orgId != null && !orgId.equals("no-org")) {
                    TenantContext.setOrganizationId(UUID.fromString(orgId));
                    log.debug("Set tenant context to organization: {}", orgId);
                } else {
                    log.warn("No organization ID found in token for request: {}", request.getRequestURI());
                }
                
            } catch (Exception e) {
                log.error("Error extracting tenant from token: {}", e.getMessage());
                // No fallar el request, solo loggear el error
            }
        }
        
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                               Object handler, Exception ex) {
        // IMPORTANTE: Limpiar el contexto al final del request
        TenantContext.clear();
    }
    
    /**
     * Determina si un endpoint es público (no requiere tenant context)
     */
    private boolean isPublicEndpoint(String uri) {
        return uri.startsWith("/api/auth/") || 
               uri.startsWith("/ping") || 
               uri.startsWith("/swagger") ||
               uri.startsWith("/v3/api-docs") ||
               uri.startsWith("/api/test/"); // Para tus endpoints de testing
    }
}