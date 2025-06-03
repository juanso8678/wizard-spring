package com.wizard.core.dto;

import lombok.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSedeResponse {
    private UUID id;
    private UUID userId;
    private String username;
    private String userEmail;
    private String userName;
    private UUID sedeId;
    private String sedeName;
    private String sedeAddress;
    private UUID organizationId;
    private String organizationName;
}