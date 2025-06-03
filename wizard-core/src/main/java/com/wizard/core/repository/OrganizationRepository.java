package com.wizard.core.repository;

import com.wizard.core.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrganizationRepository extends JpaRepository<Organization, UUID> {

    // Buscar por nombre exacto
    Optional<Organization> findByName(String name);

    // Validar existencia por nombre
    boolean existsByName(String name);

    // Buscar por correo de contacto (si implementas registro o validación de contacto)
    Optional<Organization> findByContactEmail(String email);
}
