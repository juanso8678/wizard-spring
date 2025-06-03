package com.wizard.core.mapper;

import com.wizard.core.dto.OrganizationRequest;
import com.wizard.core.dto.OrganizationResponse;
import com.wizard.core.entity.Organization;

public class OrganizationMapper {

    public static Organization toEntity(OrganizationRequest request) {
        return Organization.builder()
                .name(request.getName())
                .logo(request.getLogo())
                .contactEmail(request.getContactEmail())  // <- corregido
                .contactPhone(request.getContactPhone())  // <- corregido
                .address(request.getAddress())
                .description(request.getDescription())
                .build();
    }

    public static OrganizationResponse toResponse(Organization organization) {
        return OrganizationResponse.builder()
                .id(organization.getId())
                .name(organization.getName())
                .logo(organization.getLogo())
                .contactEmail(organization.getContactEmail())
                .contactPhone(organization.getContactPhone())
                .address(organization.getAddress())
                .description(organization.getDescription())
                .build();
    }
}
