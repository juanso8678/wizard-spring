// ============================================================================
// 2. RequiresPermission.java - Anotación corregida
// ============================================================================
package com.wizard.core.security.annotation;

import com.wizard.core.enums.PermissionCode;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotación para validar permisos en métodos de controladores
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresPermission {
    /**
     * Permiso principal requerido
     */
    PermissionCode value();
    
    /**
     * Permisos adicionales opcionales
     */
    PermissionCode[] additional() default {};
    
    /**
     * Si es true, requiere TODOS los permisos (AND)
     * Si es false, requiere AL MENOS UNO (OR)
     */
    boolean requireAll() default false;
    
    /**
     * Mensaje personalizado cuando no se tienen permisos
     */
    String message() default "Permisos insuficientes para esta operación";
    
    /**
     * Si es true, permite bypass para super administradores
     */
    boolean allowSuperAdminBypass() default true;
}
