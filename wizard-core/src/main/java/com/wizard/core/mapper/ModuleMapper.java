package com.wizard.core.mapper;

import com.wizard.core.dto.ModuleResponse;
import com.wizard.core.entity.Module;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModuleMapper {

    private final PermissionMapper permissionMapper;

    public ModuleResponse toResponse(Module module) {
        return ModuleResponse.builder()
                .id(module.getId())
                .name(module.getName())
                .displayName(module.getDisplayName())
                .description(module.getDescription())
                .icon(module.getIcon())
                .active(module.isActive())
                .build();
    }
}