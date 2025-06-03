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
@Table(name = "permissions")
public class Permission {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    @ManyToOne
    @JoinColumn(name = "function_id", nullable = false)
    private Function function;

    @Column(nullable = false, unique = true)
    private String code; // PACS_VIEW, PACS_CONFIGURE, VISOR_WEB_VIEW, etc.

    @Column(nullable = false)
    private String displayName; // "Ver PACS", "Configurar PACS"

    private String description;
    private boolean active = true;
}