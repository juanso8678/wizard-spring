package com.wizard.core.mapper;

import com.wizard.core.dto.*;
import com.wizard.core.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RoleMapper {

    private final PermissionMapper permissionMapper;

    public Role toEntity(RoleRequest request) {
        return Role.builder()
                .name(request.getName())
                .displayName(request.getDisplayName())
                .description(request.getDescription())
                .organizationId(request.getOrganizationId())
                .active(true)
                .build();
    }

    public RoleResponse toResponse(Role role) {
        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .displayName(role.getDisplayName())
                .description(role.getDescription())
                .organizationId(role.getOrganizationId())
                .active(role.isActive())
                .permissions(role.getPermissions() != null ? 
                    role.getPermissions().stream()
                        .map(permissionMapper::toResponse)
                        .collect(Collectors.toList()) : 
                    List.of())
                .build();
    }
}