package com.wizard.core.repository;

import com.wizard.core.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByName(String name);
    List<Role> findByActiveTrue();
    List<Role> findByOrganizationId(UUID organizationId);
    List<Role> findByOrganizationIdIsNull(); // Roles globales
    boolean existsByName(String name);
}
