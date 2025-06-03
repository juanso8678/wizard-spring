package com.wizard.core.modules.pacs.repository;

import com.wizard.core.modules.pacs.entity.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudyRepository extends JpaRepository<Study, UUID> {
    
    // Búsquedas básicas con filtrado por organización
    List<Study> findByOrganizationId(UUID organizationId);
    
    Optional<Study> findByStudyInstanceUID(String studyInstanceUID);
    
    Optional<Study> findByStudyInstanceUIDAndOrganizationId(String studyInstanceUID, UUID organizationId);
    
    // Búsquedas por paciente
    List<Study> findByPatientIdAndOrganizationId(String patientId, UUID organizationId);
    
    List<Study> findByPatientNameContainingIgnoreCaseAndOrganizationId(String patientName, UUID organizationId);
    
    // Búsquedas por fecha
    List<Study> findByStudyDateAndOrganizationId(LocalDate studyDate, UUID organizationId);
    
    List<Study> findByStudyDateBetweenAndOrganizationId(LocalDate startDate, LocalDate endDate, UUID organizationId);
    
    // Búsquedas por modalidad
    List<Study> findByModalityAndOrganizationId(String modality, UUID organizationId);
    
    // Búsquedas por accession number
    Optional<Study> findByAccessionNumberAndOrganizationId(String accessionNumber, UUID organizationId);
    
    // Búsquedas combinadas con query personalizada
    @Query("SELECT s FROM Study s WHERE " +
           "(:patientId IS NULL OR s.patientId = :patientId) AND " +
           "(:patientName IS NULL OR LOWER(s.patientName) LIKE LOWER(CONCAT('%', :patientName, '%'))) AND " +
           "(:studyDate IS NULL OR s.studyDate = :studyDate) AND " +
           "(:modality IS NULL OR s.modality = :modality) AND " +
           "(:accessionNumber IS NULL OR s.accessionNumber = :accessionNumber) AND " +
           "s.organizationId = :organizationId " +
           "ORDER BY s.studyDate DESC, s.studyTime DESC")
    List<Study> findStudiesWithFilters(
        @Param("patientId") String patientId,
        @Param("patientName") String patientName,
        @Param("studyDate") LocalDate studyDate,
        @Param("modality") String modality,
        @Param("accessionNumber") String accessionNumber,
        @Param("organizationId") UUID organizationId
    );
    
    // Estadísticas
    @Query("SELECT COUNT(s) FROM Study s WHERE s.organizationId = :organizationId")
    Long countByOrganizationId(@Param("organizationId") UUID organizationId);
    
    @Query("SELECT s.modality, COUNT(s) FROM Study s WHERE s.organizationId = :organizationId GROUP BY s.modality")
    List<Object[]> countByModalityAndOrganizationId(@Param("organizationId") UUID organizationId);
}