package com.wizard.core.repository;

import com.wizard.core.entity.Function;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FunctionRepository extends JpaRepository<Function, UUID> {
    Optional<Function> findByName(String name);
    List<Function> findByActiveTrue();
    boolean existsByName(String name);
}