package com.wizard.core.config;

// ✅ AGREGAR ESTOS IMPORTS ESPECÍFICOS
import com.wizard.core.entity.Module;
import com.wizard.core.entity.Function;
import com.wizard.core.entity.Permission;
import com.wizard.core.entity.Role;
import com.wizard.core.entity.User;
import com.wizard.core.repository.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID; // ✅ IMPORT ESPECÍFICO PARA UUID

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ModuleRepository moduleRepository;
    private final FunctionRepository functionRepository;
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("🚀 [DATA INITIALIZER] Starting system data initialization...");
        
        try {
            initializeModules();
            initializeFunctions();
            initializePermissions();
            initializeRoles();
            
            log.info("✅ [DATA INITIALIZER] System data initialization completed successfully!");
            
        } catch (Exception e) {
            log.error("❌ [DATA INITIALIZER] Error during initialization: {}", e.getMessage(), e);
        }
    }

    private void initializeModules() {
        log.info("📦 [MODULES] Initializing system modules...");
        
        createModuleIfNotExists("PACS", "Sistema PACS", 
            "Gestión de imágenes médicas DICOM", "🏥");
        
        createModuleIfNotExists("VISOR_WEB", "Visor Web", 
            "Visualización de imágenes en navegador", "🖥️");
        
        createModuleIfNotExists("ENRUTAMIENTO", "Enrutamiento", 
            "Gestión de rutas y AE Titles", "🔀");
        
        createModuleIfNotExists("ADMINISTRACION", "Administración", 
            "Gestión de usuarios y organizaciones", "⚙️");
        
        createModuleIfNotExists("REPORTES", "Reportes", 
            "Generación de reportes y estadísticas", "📊");
        
        createModuleIfNotExists("CONFIGURACION", "Configuración", 
            "Configuración del sistema", "🔧");
        
        log.info("✅ [MODULES] Modules initialization completed");
    }

    private void initializeFunctions() {
        log.info("⚡ [FUNCTIONS] Initializing system functions...");
        
        createFunctionIfNotExists("VIEW", "Ver", "Visualizar información");
        createFunctionIfNotExists("CREATE", "Crear", "Crear nuevos registros");
        createFunctionIfNotExists("EDIT", "Editar", "Modificar registros existentes");
        createFunctionIfNotExists("DELETE", "Eliminar", "Eliminar registros");
        createFunctionIfNotExists("CONFIGURE", "Configurar", "Configurar parámetros del sistema");
        createFunctionIfNotExists("EXPORT", "Exportar", "Exportar datos");
        createFunctionIfNotExists("IMPORT", "Importar", "Importar datos");
        createFunctionIfNotExists("APPROVE", "Aprobar", "Aprobar procesos o documentos");
        
        log.info("✅ [FUNCTIONS] Functions initialization completed");
    }

    private void initializePermissions() {
        log.info("🔐 [PERMISSIONS] Initializing system permissions...");
        
        // PACS Permissions
        createPermissionIfNotExists("PACS_VIEW", "Ver PACS", 
            "Visualizar estudios e imágenes DICOM", "PACS", "VIEW");
        createPermissionIfNotExists("PACS_CONFIGURE", "Configurar PACS", 
            "Configurar parámetros del sistema PACS", "PACS", "CONFIGURE");
        createPermissionIfNotExists("PACS_EXPORT", "Exportar desde PACS", 
            "Exportar estudios e imágenes", "PACS", "EXPORT");
        createPermissionIfNotExists("PACS_IMPORT", "Importar a PACS", 
            "Importar estudios DICOM", "PACS", "IMPORT");

        // Visor Web Permissions
        createPermissionIfNotExists("VISOR_WEB_VIEW", "Ver Visor Web", 
            "Acceder al visor web de imágenes", "VISOR_WEB", "VIEW");
        createPermissionIfNotExists("VISOR_WEB_EXPORT", "Exportar desde Visor", 
            "Exportar imágenes desde el visor web", "VISOR_WEB", "EXPORT");

        // Enrutamiento Permissions
        createPermissionIfNotExists("ENRUTAMIENTO_VIEW", "Ver Enrutamiento", 
            "Visualizar configuración de rutas", "ENRUTAMIENTO", "VIEW");
        createPermissionIfNotExists("ENRUTAMIENTO_CREATE", "Crear Rutas", 
            "Crear nuevas rutas de enrutamiento", "ENRUTAMIENTO", "CREATE");
        createPermissionIfNotExists("ENRUTAMIENTO_EDIT", "Editar Rutas", 
            "Modificar rutas existentes", "ENRUTAMIENTO", "EDIT");
        createPermissionIfNotExists("ENRUTAMIENTO_DELETE", "Eliminar Rutas", 
            "Eliminar rutas de enrutamiento", "ENRUTAMIENTO", "DELETE");
        createPermissionIfNotExists("ENRUTAMIENTO_CONFIGURE", "Configurar AE Titles", 
            "Configurar AE Titles y parámetros DICOM", "ENRUTAMIENTO", "CONFIGURE");

        // Administración Permissions
        createPermissionIfNotExists("ADMIN_USERS_VIEW", "Ver Usuarios", 
            "Visualizar lista de usuarios", "ADMINISTRACION", "VIEW");
        createPermissionIfNotExists("ADMIN_USERS_CREATE", "Crear Usuarios", 
            "Crear nuevos usuarios del sistema", "ADMINISTRACION", "CREATE");
        createPermissionIfNotExists("ADMIN_USERS_EDIT", "Editar Usuarios", 
            "Modificar información de usuarios", "ADMINISTRACION", "EDIT");
        createPermissionIfNotExists("ADMIN_USERS_DELETE", "Eliminar Usuarios", 
            "Eliminar usuarios del sistema", "ADMINISTRACION", "DELETE");
        createPermissionIfNotExists("ADMIN_ROLES_MANAGE", "Gestionar Roles", 
            "Crear y modificar roles y permisos", "ADMINISTRACION", "CONFIGURE");
        createPermissionIfNotExists("ADMIN_ORGS_MANAGE", "Gestionar Organizaciones", 
            "Administrar organizaciones", "ADMINISTRACION", "CONFIGURE");

        // Reportes Permissions
        createPermissionIfNotExists("REPORTES_VIEW", "Ver Reportes", 
            "Visualizar reportes del sistema", "REPORTES", "VIEW");
        createPermissionIfNotExists("REPORTES_EXPORT", "Exportar Reportes", 
            "Exportar reportes en diferentes formatos", "REPORTES", "EXPORT");
        createPermissionIfNotExists("REPORTES_CREATE", "Crear Reportes", 
            "Crear reportes personalizados", "REPORTES", "CREATE");

        // Configuración Permissions
        createPermissionIfNotExists("CONFIG_SYSTEM", "Configurar Sistema", 
            "Configurar parámetros generales del sistema", "CONFIGURACION", "CONFIGURE");
        
        log.info("✅ [PERMISSIONS] Permissions initialization completed");
    }

    private void initializeRoles() {
        log.info("👥 [ROLES] Initializing system roles...");
        
        // Super Admin - Acceso total
        createRoleWithPermissions("SUPER_ADMIN", "Super Administrador", 
            "Acceso total al sistema", null, List.of(
                "PACS_VIEW", "PACS_CONFIGURE", "PACS_EXPORT", "PACS_IMPORT",
                "VISOR_WEB_VIEW", "VISOR_WEB_EXPORT",
                "ENRUTAMIENTO_VIEW", "ENRUTAMIENTO_CREATE", "ENRUTAMIENTO_EDIT", 
                "ENRUTAMIENTO_DELETE", "ENRUTAMIENTO_CONFIGURE",
                "ADMIN_USERS_VIEW", "ADMIN_USERS_CREATE", "ADMIN_USERS_EDIT", 
                "ADMIN_USERS_DELETE", "ADMIN_ROLES_MANAGE", "ADMIN_ORGS_MANAGE",
                "REPORTES_VIEW", "REPORTES_EXPORT", "REPORTES_CREATE",
                "CONFIG_SYSTEM"
            ));

        // Administrador de Organización
        createRoleWithPermissions("ADMIN_ORGANIZACION", "Administrador de Organización", 
            "Administrador con acceso completo a su organización", null, List.of(
                "PACS_VIEW", "PACS_CONFIGURE", "PACS_EXPORT",
                "VISOR_WEB_VIEW", "VISOR_WEB_EXPORT",
                "ENRUTAMIENTO_VIEW", "ENRUTAMIENTO_CREATE", "ENRUTAMIENTO_EDIT", "ENRUTAMIENTO_DELETE",
                "ADMIN_USERS_VIEW", "ADMIN_USERS_CREATE", "ADMIN_USERS_EDIT", "ADMIN_ROLES_MANAGE",
                "REPORTES_VIEW", "REPORTES_EXPORT", "REPORTES_CREATE"
            ));

        // Técnico Radiólogo
        createRoleWithPermissions("TECNICO_RADIOLOGO", "Técnico Radiólogo", 
            "Acceso a PACS y visor web para trabajo diario", null, List.of(
                "PACS_VIEW", "PACS_EXPORT",
                "VISOR_WEB_VIEW", "VISOR_WEB_EXPORT",
                "REPORTES_VIEW"
            ));

        // Administrador PACS
        createRoleWithPermissions("ADMIN_PACS", "Administrador PACS", 
            "Administrador técnico del sistema PACS", null, List.of(
                "PACS_VIEW", "PACS_CONFIGURE", "PACS_EXPORT", "PACS_IMPORT",
                "ENRUTAMIENTO_VIEW", "ENRUTAMIENTO_CREATE", "ENRUTAMIENTO_EDIT", "ENRUTAMIENTO_CONFIGURE",
                "REPORTES_VIEW", "REPORTES_CREATE"
            ));

        // Operador Visor
        createRoleWithPermissions("OPERADOR_VISOR", "Operador Visor Web", 
            "Acceso solo al visor web", null, List.of(
                "VISOR_WEB_VIEW", "VISOR_WEB_EXPORT"
            ));

        // Auditor
        createRoleWithPermissions("AUDITOR", "Auditor", 
            "Acceso de solo lectura para auditorías", null, List.of(
                "PACS_VIEW",
                "VISOR_WEB_VIEW",
                "ENRUTAMIENTO_VIEW",
                "ADMIN_USERS_VIEW",
                "REPORTES_VIEW", "REPORTES_EXPORT"
            ));
        
        log.info("✅ [ROLES] Roles initialization completed");
    }

    // Métodos helper
    private void createModuleIfNotExists(String name, String displayName, String description, String icon) {
        if (!moduleRepository.existsByName(name)) {
            Module module = Module.builder()
                    .name(name)
                    .displayName(displayName)
                    .description(description)
                    .icon(icon)
                    .active(true)
                    .build();
            
            moduleRepository.save(module);
            log.debug("📦 Created module: {}", displayName);
        }
    }

    private void createFunctionIfNotExists(String name, String displayName, String description) {
        if (!functionRepository.existsByName(name)) {
            Function function = Function.builder()
                    .name(name)
                    .displayName(displayName)
                    .description(description)
                    .active(true)
                    .build();
            
            functionRepository.save(function);
            log.debug("⚡ Created function: {}", displayName);
        }
    }

    private void createPermissionIfNotExists(String code, String displayName, String description, 
                                           String moduleName, String functionName) {
        if (!permissionRepository.existsByCode(code)) {
            Optional<Module> module = moduleRepository.findByName(moduleName);
            Optional<Function> function = functionRepository.findByName(functionName);
            
            if (module.isPresent() && function.isPresent()) {
                Permission permission = Permission.builder()
                        .code(code)
                        .displayName(displayName)
                        .description(description)
                        .module(module.get())
                        .function(function.get())
                        .active(true)
                        .build();
                
                permissionRepository.save(permission);
                log.debug("🔐 Created permission: {}", displayName);
            } else {
                log.warn("⚠️ Cannot create permission {}: Module or Function not found", code);
            }
        }
    }

    private void createRoleWithPermissions(String name, String displayName, String description, 
                                         java.util.UUID organizationId, List<String> permissionCodes) {
        if (!roleRepository.existsByName(name)) {
            Role role = Role.builder()
                    .name(name)
                    .displayName(displayName)
                    .description(description)
                    .organizationId(organizationId)
                    .active(true)
                    .build();
            
            // Buscar permisos por código
            List<Permission> permissions = new ArrayList<>();
            for (String permissionCode : permissionCodes) {
                Optional<Permission> permission = permissionRepository.findByCode(permissionCode);
                if (permission.isPresent()) {
                    permissions.add(permission.get());
                } else {
                    log.warn("⚠️ Permission not found: {}", permissionCode);
                }
            }
            
            role.setPermissions(permissions);
            roleRepository.save(role);
            
            log.info("👥 Created role: {} with {} permissions", displayName, permissions.size());
        }
    }
}