package com.wizard.core.mapper;

import com.wizard.core.dto.*;
import com.wizard.core.entity.*;
import org.springframework.stereotype.Component;

@Component
public class UserRoleMapper {

    public UserRole toEntity(UserRoleRequest request, User user, Role role) {
        return UserRole.builder()
                .user(user)
                .role(role)
                .organizationId(request.getOrganizationId())
                .active(true)
                .build();
    }

    public UserRoleResponse toResponse(UserRole userRole) {
        return UserRoleResponse.builder()
                .id(userRole.getId())
                .userId(userRole.getUser().getId())
                .username(userRole.getUser().getUsername())
                .userEmail(userRole.getUser().getEmail())
                .roleId(userRole.getRole().getId())
                .roleName(userRole.getRole().getName())
                .roleDisplayName(userRole.getRole().getDisplayName())
                .organizationId(userRole.getOrganizationId())
                .active(userRole.isActive())
                .build();
    }
}
