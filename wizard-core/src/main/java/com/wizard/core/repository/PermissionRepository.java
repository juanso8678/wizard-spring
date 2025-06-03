package com.wizard.core.repository;

import com.wizard.core.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PermissionRepository extends JpaRepository<Permission, UUID> {
    Optional<Permission> findByCode(String code);
    List<Permission> findByActiveTrue();
    List<Permission> findByModuleId(UUID moduleId);
    List<Permission> findByFunctionId(UUID functionId);
    boolean existsByCode(String code);
}