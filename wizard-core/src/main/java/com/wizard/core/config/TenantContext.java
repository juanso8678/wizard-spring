package com.wizard.core.config;

import lombok.extern.slf4j.Slf4j;
import java.util.UUID;

/**
 * Contexto de tenant/organización para multi-tenancy
 * Maneja el contexto de la organización actual para cada hilo de ejecución
 */
@Slf4j
public class TenantContext {
    
    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();
    private static final ThreadLocal<UUID> CURRENT_ORGANIZATION = new ThreadLocal<>();
    private static final ThreadLocal<UUID> CURRENT_USER = new ThreadLocal<>();
    
    /**
     * Establece el tenant ID actual (como String)
     * @param tenantId ID del tenant
     */
    public static void setCurrentTenant(String tenantId) {
        if (tenantId != null) {
            CURRENT_TENANT.set(tenantId);
            log.debug("🏢 [TENANT] Set tenant: {}", tenantId);
        } else {
            CURRENT_TENANT.remove();
            log.debug("🏢 [TENANT] Cleared tenant");
        }
    }
    
    /**
     * Obtiene el tenant ID actual
     * @return ID del tenant actual o null si no está establecido
     */
    public static String getCurrentTenant() {
        return CURRENT_TENANT.get();
    }
    
    /**
     * Establece la organización actual
     * @param organizationId UUID de la organización
     */
    public static void setOrganizationId(UUID organizationId) {
        if (organizationId != null) {
            CURRENT_ORGANIZATION.set(organizationId);
            // También establecer como tenant string para compatibilidad
            setCurrentTenant(organizationId.toString());
            log.debug("🏢 [ORG] Set organization: {}", organizationId);
        } else {
            CURRENT_ORGANIZATION.remove();
            setCurrentTenant(null);
            log.debug("🏢 [ORG] Cleared organization");
        }
    }
    
    /**
     * Obtiene la organización actual
     * @return UUID de la organización actual o null si no está establecida
     */
    public static UUID getOrganizationId() {
        return CURRENT_ORGANIZATION.get();
    }
    
    /**
     * Establece el usuario actual
     * @param userId UUID del usuario
     */
    public static void setCurrentUser(UUID userId) {
        if (userId != null) {
            CURRENT_USER.set(userId);
            log.debug("👤 [USER] Set user: {}", userId);
        } else {
            CURRENT_USER.remove();
            log.debug("👤 [USER] Cleared user");
        }
    }
    
    /**
     * Obtiene el usuario actual
     * @return UUID del usuario actual o null si no está establecido
     */
    public static UUID getCurrentUser() {
        return CURRENT_USER.get();
    }
    
    /**
     * Establece tanto organización como tenant de una vez
     * @param organizationId UUID de la organización
     */
    public static void setContext(UUID organizationId) {
        setOrganizationId(organizationId);
    }
    
    /**
     * Establece el contexto completo
     * @param userId UUID del usuario
     * @param organizationId UUID de la organización
     */
    public static void setFullContext(UUID userId, UUID organizationId) {
        setCurrentUser(userId);
        setOrganizationId(organizationId);
        log.debug("🔧 [CONTEXT] Set full context - User: {}, Org: {}", userId, organizationId);
    }
    
    /**
     * Verifica si hay un tenant establecido
     * @return true si hay tenant, false en caso contrario
     */
    public static boolean hasTenant() {
        return getCurrentTenant() != null;
    }
    
    /**
     * Verifica si hay una organización establecida
     * @return true si hay organización, false en caso contrario
     */
    public static boolean hasOrganization() {
        return getOrganizationId() != null;
    }
    
    /**
     * Verifica si hay un usuario establecido
     * @return true si hay usuario, false en caso contrario
     */
    public static boolean hasUser() {
        return getCurrentUser() != null;
    }
    
    /**
     * Limpia todo el contexto del hilo actual
     */
    public static void clear() {
        CURRENT_TENANT.remove();
        CURRENT_ORGANIZATION.remove();
        CURRENT_USER.remove();
        log.debug("🧹 [CONTEXT] Cleared all context");
    }
    
    /**
     * Obtiene un resumen del contexto actual
     * @return String con información del contexto
     */
    public static String getContextSummary() {
        return String.format("TenantContext[tenant=%s, org=%s, user=%s]", 
                           getCurrentTenant(), 
                           getOrganizationId(), 
                           getCurrentUser());
    }
    
    /**
     * Ejecuta un bloque de código con un contexto específico
     * @param organizationId Organización para el contexto
     * @param runnable Código a ejecutar
     */
    public static void executeWithContext(UUID organizationId, Runnable runnable) {
        UUID previousOrg = getOrganizationId();
        String previousTenant = getCurrentTenant();
        
        try {
            setOrganizationId(organizationId);
            runnable.run();
        } finally {
            // Restaurar contexto anterior
            if (previousOrg != null) {
                setOrganizationId(previousOrg);
            } else {
                setOrganizationId(null);
            }
        }
    }
    
    /**
     * Ejecuta un bloque de código con contexto completo
     * @param userId Usuario para el contexto
     * @param organizationId Organización para el contexto
     * @param runnable Código a ejecutar
     */
    public static void executeWithFullContext(UUID userId, UUID organizationId, Runnable runnable) {
        UUID previousUser = getCurrentUser();
        UUID previousOrg = getOrganizationId();
        String previousTenant = getCurrentTenant();
        
        try {
            setFullContext(userId, organizationId);
            runnable.run();
        } finally {
            // Restaurar contexto anterior
            clear();
            if (previousUser != null) setCurrentUser(previousUser);
            if (previousOrg != null) setOrganizationId(previousOrg);
        }
    }
}