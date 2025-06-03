package com.wizard.core.repository;

import com.wizard.core.entity.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ModuleRepository extends JpaRepository<Module, UUID> {
    Optional<Module> findByName(String name);
    List<Module> findByActiveTrue();
    boolean existsByName(String name);
}