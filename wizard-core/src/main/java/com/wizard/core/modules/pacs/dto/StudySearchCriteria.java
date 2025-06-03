// ============================================================================
// 1. StudySearchCriteria.java - Criterios de búsqueda de estudios
// ============================================================================
package com.wizard.core.modules.pacs.dto;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudySearchCriteria {
    
    // Información del paciente
    private String patientId;
    private String patientName;
    
    // Información del estudio
    private String studyInstanceUID;
    private LocalDate studyDate;
    private LocalDate studyDateFrom;
    private LocalDate studyDateTo;
    private String accessionNumber;
    private String studyDescription;
    private String referringPhysician;
    
    // Información técnica
    private String modality;
    private String institutionName;
    private String stationName;
    
    // Parámetros de paginación
    private Integer limit;
    private Integer offset;
    
    // Filtros adicionales
    private String studyStatus;
    private Boolean includeMetadata;
    
    /**
     * Crea criterios básicos de búsqueda
     */
    public static StudySearchCriteria basic() {
        return StudySearchCriteria.builder()
                .limit(50)
                .offset(0)
                .includeMetadata(false)
                .build();
    }
    
    /**
     * Crea criterios de búsqueda por paciente
     */
    public static StudySearchCriteria forPatient(String patientId) {
        return StudySearchCriteria.builder()
                .patientId(patientId)
                .limit(100)
                .offset(0)
                .includeMetadata(true)
                .build();
    }
    
    /**
     * Crea criterios de búsqueda por rango de fechas
     */
    public static StudySearchCriteria forDateRange(LocalDate from, LocalDate to) {
        return StudySearchCriteria.builder()
                .studyDateFrom(from)
                .studyDateTo(to)
                .limit(200)
                .offset(0)
                .includeMetadata(false)
                .build();
    }
    
    /**
     * Verifica si los criterios están vacíos
     */
    public boolean isEmpty() {
        return patientId == null && 
               patientName == null && 
               studyInstanceUID == null &&
               studyDate == null && 
               studyDateFrom == null && 
               studyDateTo == null &&
               accessionNumber == null && 
               modality == null;
    }
}

// ============================================================================
//