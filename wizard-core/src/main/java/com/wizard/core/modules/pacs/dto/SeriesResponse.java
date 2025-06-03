package com.wizard.core.modules.pacs.dto;

import lombok.*;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeriesResponse {
    private UUID id;
    private String seriesInstanceUID;
    private String seriesNumber;
    private String seriesDescription;
    private String modality;
    private LocalTime seriesTime;
    private String bodyPartExamined;
    private String protocolName;
    private String manufacturer;
    private Integer numberOfInstances;
    private String status;
    private LocalDateTime createdAt;
    
    // Instances (optional, for detailed view)
    private List<InstanceResponse> instances;
}