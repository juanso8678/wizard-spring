package com.wizard.core.modules.pacs.dto;

import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyRequest {
    private String patientId;
    private String patientName;
    private String patientBirthDate;
    private String patientSex;
    private LocalDate studyDate;
    private String studyDescription;
    private String accessionNumber;
    private String referringPhysician;
    private String modality;
    private String institutionName;
    private UUID organizationId;
}