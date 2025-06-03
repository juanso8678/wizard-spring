package com.wizard.core.dto;

import lombok.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SedeResponse {
    private UUID id;
    private String name;
    private String address;
    private String contact;
    private String email;
    private String phone;
    private String typeSede;
    private String description;
    private UUID organizationId;
}