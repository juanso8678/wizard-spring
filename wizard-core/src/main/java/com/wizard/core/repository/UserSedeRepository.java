package com.wizard.core.repository;

import com.wizard.core.entity.UserSede;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.wizard.core.entity.Sede;
import com.wizard.core.entity.User;

public interface UserSedeRepository extends JpaRepository<UserSede, UUID> {
    // Buscar todas las sedes asignadas a un usuario
    List<UserSede> findByUserId(UUID userId);

    // Buscar todas las relaciones por sede (usuarios de esa sede)
    List<UserSede> findBySedeId(UUID sedeId);

    // Validar si un usuario está asignado a una sede específica
    Optional<UserSede> findByUserAndSede(User user, Sede sede);

    // Validar si un usuario tiene acceso a una sede por sus UUIDs
    boolean existsByUserIdAndSedeId(UUID userId, UUID sedeId);

}
