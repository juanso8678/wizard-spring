package com.wizard.core.modules.pacs.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyResponse {
    private UUID id;
    private String studyInstanceUID;
    
    // Patient Information
    private String patientId;
    private String patientName;
    private String patientBirthDate;
    private String patientSex;
    
    // Study Information
    private LocalDate studyDate;
    private LocalTime studyTime;
    private String studyDescription;
    private String accessionNumber;
    private String referringPhysician;
    private String modality;
    private String institutionName;
    
    // Metadata
    private Integer numberOfSeries;
    private Integer numberOfInstances;
    private String status;
    private UUID organizationId;
    private LocalDateTime createdAt;
    
    // Series (optional, for detailed view)
    private List<SeriesResponse> series;
}