package com.wizard.core.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private UUID id;
    private String email;
    private String username;
    private String name;
    private String role;
    private boolean activo;
    private UUID organizationId;
}
