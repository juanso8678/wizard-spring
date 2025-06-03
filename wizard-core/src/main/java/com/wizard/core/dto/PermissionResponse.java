package com.wizard.core.dto;

import lombok.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionResponse {
    private UUID id;
    private String code;
    private String displayName;
    private String description;
    private String moduleName;
    private String moduleDisplayName;
    private String functionName;
    private String functionDisplayName;
    private boolean active;
}
