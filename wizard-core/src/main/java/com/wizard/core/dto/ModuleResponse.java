package com.wizard.core.dto;

import lombok.*;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModuleResponse {
    private UUID id;
    private String name;
    private String displayName;
    private String description;
    private String icon;
    private boolean active;
    private List<PermissionResponse> permissions;
}