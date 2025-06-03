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
@Table(name = "modules")
public class Module {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name; // PACS, VISOR_WEB, ENRUTAMIENTO, ADMINISTRACION

    @Column(nullable = false)
    private String displayName; // "Sistema PACS", "Visor Web", "Enrutamiento"

    private String description;
    private String icon; // Para UI
    private boolean active = true;
}