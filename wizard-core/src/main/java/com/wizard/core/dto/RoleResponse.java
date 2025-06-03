package com.wizard.core.dto;

import lombok.*;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleResponse {
    private UUID id;
    private String name;
    private String displayName;
    private String description;
    private UUID organizationId;
    private boolean active;
    private List<PermissionResponse> permissions;
}