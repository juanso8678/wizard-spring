package com.wizard.core.dto;

import lombok.*;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleRequest {
    private String name;
    private String displayName;
    private String description;
    private UUID organizationId; // null para roles globales
    private List<UUID> permissionIds;
}