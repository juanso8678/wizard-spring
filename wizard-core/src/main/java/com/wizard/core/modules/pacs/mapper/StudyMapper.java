package com.wizard.core.modules.pacs.mapper;

import com.wizard.core.modules.pacs.dto.StudyRequest;
import com.wizard.core.modules.pacs.dto.StudyResponse;
import com.wizard.core.modules.pacs.entity.Study;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class StudyMapper {

    private final SeriesMapper seriesMapper;

    public Study toEntity(StudyRequest request) {
        return Study.builder()
                .studyInstanceUID(generateStudyInstanceUID())
                .patientId(request.getPatientId())
                .patientName(request.getPatientName())
                .patientBirthDate(request.getPatientBirthDate())
                .patientSex(request.getPatientSex())
                .studyDate(request.getStudyDate())
                .studyDescription(request.getStudyDescription())
                .accessionNumber(request.getAccessionNumber())
                .referringPhysician(request.getReferringPhysician())
                .modality(request.getModality())
                .institutionName(request.getInstitutionName())
                .organizationId(request.getOrganizationId())
                .status("RECEIVED")
                .build();
    }

    public StudyResponse toResponse(Study study) {
        return StudyResponse.builder()
                .id(study.getId())
                .studyInstanceUID(study.getStudyInstanceUID())
                .patientId(study.getPatientId())
                .patientName(study.getPatientName())
                .patientBirthDate(study.getPatientBirthDate())
                .patientSex(study.getPatientSex())
                .studyDate(study.getStudyDate())
                .studyTime(study.getStudyTime())
                .studyDescription(study.getStudyDescription())
                .accessionNumber(study.getAccessionNumber())
                .referringPhysician(study.getReferringPhysician())
                .modality(study.getModality())
                .institutionName(study.getInstitutionName())
                .numberOfSeries(study.getNumberOfSeries())
                .numberOfInstances(study.getNumberOfInstances())
                .status(study.getStatus())
                .organizationId(study.getOrganizationId())
                .createdAt(study.getCreatedAt())
                .build();
    }

    public StudyResponse toResponseWithSeries(Study study) {
        StudyResponse response = toResponse(study);
        
        if (study.getSeries() != null) {
            List<com.wizard.core.modules.pacs.dto.SeriesResponse> seriesResponses = 
                study.getSeries().stream()
                    .map(seriesMapper::toResponse)
                    .collect(Collectors.toList());
            response.setSeries(seriesResponses);
        }
        
        return response;
    }

    private String generateStudyInstanceUID() {
        // Generar UID único para el estudio
        return "1.2.826.0.1.3680043.2.1125." + System.currentTimeMillis() + "." + UUID.randomUUID().toString().replace("-", "");
    }
}