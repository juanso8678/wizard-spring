package com.wizard.core.modules.pacs.mapper;

import com.wizard.core.modules.pacs.dto.InstanceResponse;
import com.wizard.core.modules.pacs.entity.Instance;
import org.springframework.stereotype.Component;

@Component
public class InstanceMapper {

    public InstanceResponse toResponse(Instance instance) {
        return InstanceResponse.builder()
                .id(instance.getId())
                .sopInstanceUID(instance.getSopInstanceUID())
                .instanceNumber(instance.getInstanceNumber())
                .sopClassUID(instance.getSopClassUID())
                .rows(instance.getRows())
                .columns(instance.getColumns())
                .fileName(instance.getFileName())
                .fileSize(instance.getFileSize())
                .status(instance.getStatus())
                .createdAt(instance.getCreatedAt())
                .build();
    }
}