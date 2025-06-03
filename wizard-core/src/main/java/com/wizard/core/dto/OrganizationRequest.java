package com.wizard.core.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationRequest {
    private String name;
    private String logo;
    private String contactEmail;
    private String contactPhone;
    private String address;
    private String description;
}
