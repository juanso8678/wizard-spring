package com.wizard.core.mapper;

import com.wizard.core.dto.PermissionResponse;
import com.wizard.core.entity.Permission;
import org.springframework.stereotype.Component;

@Component
public class PermissionMapper {

    public PermissionResponse toResponse(Permission permission) {
        return PermissionResponse.builder()
                .id(permission.getId())
                .code(permission.getCode())
                .displayName(permission.getDisplayName())
                .description(permission.getDescription())
                .moduleName(permission.getModule().getName())
                .moduleDisplayName(permission.getModule().getDisplayName())
                .functionName(permission.getFunction().getName())
                .functionDisplayName(permission.getFunction().getDisplayName())
                .active(permission.isActive())
                .build();
    }
}