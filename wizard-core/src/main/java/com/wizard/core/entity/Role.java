package com.wizard.core.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name; // TECNICO_RADIOLOGO, ADMIN_PACS, SUPER_ADMIN

    @Column(nullable = false)
    private String displayName; // "Técnico Radiólogo", "Admin PACS"

    private String description;
    private boolean active = true;

    // Para multitenancy
    @Column(name = "organization_id")
    private UUID organizationId;

   @ManyToMany(fetch = FetchType.EAGER) 
    @JoinTable(
        name = "role_permissions",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private List<Permission> permissions;
}