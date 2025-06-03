package com.wizard.core.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationResponse {
    private UUID id;
    private String name;
    private String logo;
    private String contactEmail;
    private String contactPhone;
    private String address;
    private String description;
}
