package com.wizard.core.modules.pacs.service.interfaces;

import com.wizard.core.modules.pacs.dto.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface StudyService {
    
    // ========== MÉTODOS EXISTENTES ==========
    StudyResponse createStudy(StudyRequest request);
    List<StudyResponse> findAllStudies();
    StudyResponse findStudyById(UUID id);
    StudyResponse findStudyByInstanceUID(String studyInstanceUID);
    List<StudyResponse> searchStudies(String patientId, String patientName, 
                                    LocalDate studyDate, String modality, String accessionNumber);
    StudyResponse updateStudy(UUID id, StudyRequest request);
    void deleteStudy(UUID id);
    List<SeriesResponse> getSeriesByStudy(UUID studyId);
    Long getStudyCount();
    
    // ========== MÉTODOS FALTANTES QUE USA EL CONTROLLER ==========
    
    /**
     * Busca estudios por ID de paciente
     * @param patientId ID del paciente
     * @return Lista de estudios del paciente
     */
    List<StudyResponse> findStudiesByPatientId(String patientId);
    
    /**
     * Reprocesa un estudio (revalidar metadatos, reenviar a PACS, etc.)
     * @param id ID del estudio
     * @return Estudio actualizado
     */
    StudyResponse reprocessStudy(UUID id);
    
    // ========== MÉTODOS ADICIONALES ÚTILES ==========
    
    /**
     * Busca estudios por rango de fechas
     */
    List<StudyResponse> findStudiesByDateRange(LocalDate startDate, LocalDate endDate);
    
    /**
     * Busca estudios por modalidad
     */
    List<StudyResponse> findStudiesByModality(String modality);
    
    /**
     * Busca estudios por médico referente
     */
    List<StudyResponse> findStudiesByReferringPhysician(String physician);
    
    /**
     * Obtiene estadísticas detalladas de estudios
     */
    StudyStatsResponse getDetailedStudyStats();
}