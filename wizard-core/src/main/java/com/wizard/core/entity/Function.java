package com.wizard.core.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "functions")
public class Function {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name; // VIEW, CREATE, EDIT, DELETE, CONFIGURE

    @Column(nullable = false)
    private String displayName; // "Ver", "Crear", "Editar", "Eliminar"

    private String description;
    private boolean active = true;
}
