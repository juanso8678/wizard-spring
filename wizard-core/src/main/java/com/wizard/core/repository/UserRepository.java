package com.wizard.core.repository;

import com.wizard.core.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    
    Optional<User> findByUsernameOrEmail(String username, String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<User> findByActivoTrue();
    List<User> findByRole(String role);
    
    // ✅ SIMPLE - Sin @Query
    List<User> findByOrganizationId(UUID organizationId);
}