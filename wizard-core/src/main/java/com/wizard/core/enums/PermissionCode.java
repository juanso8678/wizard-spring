// ============================================================================
// 1. PermissionCode.java - Enum principal de permisos
// ============================================================================
package com.wizard.core.enums;

/**
 * Códigos de permisos para el sistema Wizard
 * Organizados por módulos para mejor mantenibilidad
 */
public enum PermissionCode {
    
    // ========== PERMISOS GENERALES ==========
    ADMIN_ACCESS("Acceso de administrador"),
    SUPER_ADMIN_ACCESS("Acceso de super administrador"),
    
    // ========== MÓDULO PACS ==========
    // Gestión de estudios
    PACS_VIEW("Ver estudios PACS"),
    PACS_IMPORT("Importar estudios DICOM"),
    PACS_EDIT("Editar estudios PACS"),
    PACS_DELETE("Eliminar estudios PACS"),
    PACS_EXPORT("Exportar estudios PACS"),
    
    // Gestión de series e instancias
    PACS_SERIES_VIEW("Ver series DICOM"),
    PACS_SERIES_EDIT("Editar series DICOM"),
    PACS_INSTANCE_VIEW("Ver instancias DICOM"),
    PACS_INSTANCE_EDIT("Editar instancias DICOM"),
    
    // ========== ENRUTAMIENTO DICOM ==========
    ENRUTAMIENTO_VIEW("Ver nodos DICOM"),
    ENRUTAMIENTO_CREATE("Crear nodos DICOM"),
    ENRUTAMIENTO_EDIT("Editar nodos DICOM"),
    ENRUTAMIENTO_DELETE("Eliminar nodos DICOM"),
    ENRUTAMIENTO_TEST("Probar conexiones DICOM"),
    
    // ========== VISOR WEB ==========
    VISOR_WEB_VIEW("Acceder al visor web"),
    VISOR_WEB_SHARE("Compartir enlaces del visor"),
    VISOR_WEB_DOWNLOAD("Descargar desde visor"),
    VISOR_WEB_PRINT("Imprimir desde visor"),
    
    // ========== GESTIÓN DE USUARIOS ==========
    USER_VIEW("Ver usuarios"),
    USER_CREATE("Crear usuarios"),
    USER_EDIT("Editar usuarios"),
    USER_DELETE("Eliminar usuarios"),
    USER_PERMISSIONS("Gestionar permisos de usuarios"),
    
    // ========== GESTIÓN DE ORGANIZACIONES ==========
    ORG_VIEW("Ver organizaciones"),
    ORG_CREATE("Crear organizaciones"),
    ORG_EDIT("Editar organizaciones"),
    ORG_DELETE("Eliminar organizaciones"),
    ORG_SETTINGS("Configurar organización"),
    
    // ========== REPORTES Y ESTADÍSTICAS ==========
    REPORTS_VIEW("Ver reportes"),
    REPORTS_CREATE("Crear reportes"),
    REPORTS_EXPORT("Exportar reportes"),
    STATS_VIEW("Ver estadísticas"),
    
    // ========== CONFIGURACIÓN DEL SISTEMA ==========
    SYSTEM_CONFIG("Configuración del sistema"),
    SYSTEM_LOGS("Ver logs del sistema"),
    SYSTEM_BACKUP("Realizar backups"),
    SYSTEM_MAINTENANCE("Modo mantenimiento");
    
    private final String description;
    
    PermissionCode(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Obtiene el módulo al que pertenece el permiso
     */
    public String getModule() {
        String name = this.name();
        if (name.startsWith("PACS_")) return "PACS";
        if (name.startsWith("ENRUTAMIENTO_")) return "ENRUTAMIENTO";
        if (name.startsWith("VISOR_")) return "VISOR";
        if (name.startsWith("USER_")) return "USUARIOS";
        if (name.startsWith("ORG_")) return "ORGANIZACIONES";
        if (name.startsWith("REPORTS_") || name.startsWith("STATS_")) return "REPORTES";
        if (name.startsWith("SYSTEM_")) return "SISTEMA";
        return "GENERAL";
    }
    
    /**
     * Verifica si es un permiso de administrador
     */
    public boolean isAdminPermission() {
        return this == ADMIN_ACCESS || this == SUPER_ADMIN_ACCESS || 
               this.name().startsWith("SYSTEM_") || this.name().startsWith("ORG_");
    }
}