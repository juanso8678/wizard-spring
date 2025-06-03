// ============================================================================
// TenantContextAspect.java - ASPECTO OPCIONAL para logging automático
// ============================================================================
package com.wizard.core.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * Aspecto para logging automático del contexto de tenant
 */
@Slf4j
@Aspect
@Component
public class TenantContextAspect {

    @Around("@annotation(com.wizard.core.security.annotation.RequiresPermission)")
    public Object logTenantContext(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        String context = TenantContext.getContextSummary();
        
        log.debug("🔍 [ASPECT] Executing {} with context: {}", methodName, context);
        
        try {
            return joinPoint.proceed();
        } catch (Exception e) {
            log.error("❌ [ASPECT] Error in {} with context {}: {}", 
                     methodName, context, e.getMessage());
            throw e;
        }
    }
}