package com.wizard.core.dto;

import lombok.*;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRoleResponse {
    private UUID id;
    private UUID userId;
    private String username;
    private String userEmail;
    private UUID roleId;
    private String roleName;
    private String roleDisplayName;
    private UUID organizationId;
    private boolean active;
}