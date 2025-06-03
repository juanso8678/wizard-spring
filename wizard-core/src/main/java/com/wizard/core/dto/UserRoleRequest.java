package com.wizard.core.dto;

import lombok.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRoleRequest {
    private UUID userId;
    private UUID roleId;
    private UUID organizationId;
}