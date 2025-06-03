package com.wizard.core.repository;

import com.wizard.core.entity.Sede;
import org.springframework.data.jpa.repository.JpaRepository;
import com.wizard.core.entity.Organization;
import java.util.List;
import java.util.Optional;

import java.util.UUID;

public interface SedeRepository extends JpaRepository<Sede, UUID> {
    // Buscar todas las sedes de una organización
    List<Sede> findByOrganization(Organization organization);

    // Buscar por nombre dentro de una organización
    Optional<Sede> findByNameAndOrganization(String name, Organization organization);

    // Verificar existencia por nombre dentro de una organización
    boolean existsByNameAndOrganization(String name, Organization organization);

    // Buscar por email de contacto
    Optional<Sede> findByEmail(String email);
}
