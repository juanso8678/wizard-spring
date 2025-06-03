package com.wizard.core.modules.pacs.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstanceResponse {
    private UUID id;
    private String sopInstanceUID;
    private String instanceNumber;
    private String sopClassUID;
    private Integer rows;
    private Integer columns;
    private String fileName;
    private Long fileSize;
    private String status;
    private LocalDateTime createdAt;
}