package com.wizard.core.repository;

import com.wizard.core.entity.UserRole;
import com.wizard.core.entity.User;
import com.wizard.core.entity.Role;
import com.wizard.core.enums.PermissionCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRoleRepository extends JpaRepository<UserRole, UUID> {
    
    List<UserRole> findByUserId(UUID userId);
    List<UserRole> findByRoleId(UUID roleId);
    List<UserRole> findByUserIdAndOrganizationId(UUID userId, UUID organizationId);
    Optional<UserRole> findByUserAndRole(User user, Role role);
    boolean existsByUserIdAndRoleId(UUID userId, UUID roleId);
    
    /**
     * ✅ MÉTODO EXISTENTE - Devuelve códigos como String
     * Este es el que ya tienes implementado
     */
    @Query("SELECT DISTINCT p.code FROM UserRole ur " +
           "JOIN ur.role r " +
           "JOIN r.permissions p " +
           "WHERE ur.user.id = :userId " +
           "AND ur.organizationId = :organizationId " +
           "AND ur.active = true " +
           "AND r.active = true " +
           "AND p.active = true")
    List<String> findUserPermissions(@Param("userId") UUID userId, 
                                   @Param("organizationId") UUID organizationId);
    
    /**
     * ✅ NUEVO MÉTODO - Compatible con PermissionService que creé antes
     * Este método es para mantener compatibilidad con el código anterior
     */
    @Query("SELECT DISTINCT p.code FROM UserRole ur " +
           "JOIN ur.role r " +
           "JOIN r.permissions p " +
           "WHERE ur.user.id = :userId " +
           "AND ur.organizationId = :tenantId " +
           "AND ur.active = true " +
           "AND r.active = true " +
           "AND p.active = true")
    List<String> findPermissionsByUserIdAndTenantId(@Param("userId") UUID userId, 
                                                   @Param("tenantId") String tenantId);
    
    /**
     * ✅ MÉTODO ADICIONAL - Para obtener roles del usuario
     */
    @Query("SELECT DISTINCT r FROM UserRole ur " +
           "JOIN ur.role r " +
           "WHERE ur.user.id = :userId " +
           "AND ur.organizationId = :organizationId " +
           "AND ur.active = true " +
           "AND r.active = true")
    List<Role> findUserRoles(@Param("userId") UUID userId, 
                           @Param("organizationId") UUID organizationId);
    
    /**
     * ✅ MÉTODO PARA VERIFICAR SI USUARIO TIENE PERMISO ESPECÍFICO
     */
    @Query("SELECT COUNT(p) > 0 FROM UserRole ur " +
           "JOIN ur.role r " +
           "JOIN r.permissions p " +
           "WHERE ur.user.id = :userId " +
           "AND ur.organizationId = :organizationId " +
           "AND p.code = :permissionCode " +
           "AND ur.active = true " +
           "AND r.active = true " +
           "AND p.active = true")
    boolean userHasPermission(@Param("userId") UUID userId, 
                            @Param("organizationId") UUID organizationId,
                            @Param("permissionCode") String permissionCode);
}