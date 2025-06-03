package com.wizard.core.mapper;

import org.springframework.stereotype.Component;

import com.wizard.core.dto.SedeRequest;
import com.wizard.core.dto.SedeResponse;
import com.wizard.core.entity.Organization;
import com.wizard.core.entity.Sede;
@Component
public class SedeMapper {

    public Sede toEntity(SedeRequest request, Organization organization) {
        return Sede.builder()
                .name(request.getName())
                .address(request.getAddress())
                .contact(request.getContact())
                .email(request.getEmail())
                .phone(request.getPhone())
                .typeSede(request.getTypeSede())
                .description(request.getDescription())
                .organization(organization)
                .build();
    }

    public SedeResponse toResponse(Sede sede) {
        return SedeResponse.builder()
                .id(sede.getId())
                .name(sede.getName())
                .address(sede.getAddress())
                .contact(sede.getContact())
                .email(sede.getEmail())
                .phone(sede.getPhone())
                .typeSede(sede.getTypeSede())
                .description(sede.getDescription())
                .organizationId(sede.getOrganization().getId())
                .build();
    }
}
