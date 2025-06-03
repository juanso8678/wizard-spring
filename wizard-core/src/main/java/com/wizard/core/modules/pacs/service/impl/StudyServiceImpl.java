package com.wizard.core.modules.pacs.service.impl;

import com.wizard.core.config.TenantContext;
import com.wizard.core.exception.NotFoundException;
import com.wizard.core.exception.UnauthorizedException;
import com.wizard.core.modules.pacs.dto.*;
import com.wizard.core.modules.pacs.entity.Study;
import com.wizard.core.modules.pacs.mapper.StudyMapper;
import com.wizard.core.modules.pacs.mapper.SeriesMapper;
import com.wizard.core.modules.pacs.repository.StudyRepository;
import com.wizard.core.modules.pacs.repository.SeriesRepository;
import com.wizard.core.modules.pacs.service.interfaces.StudyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors; 

@Slf4j
@Service
@RequiredArgsConstructor
public class StudyServiceImpl implements StudyService {

    private final StudyRepository studyRepository;
    private final SeriesRepository seriesRepository;
    private final StudyMapper studyMapper;
    private final SeriesMapper seriesMapper;

    // ========== MÉTODOS EXISTENTES (ya implementados) ==========
    
    @Override
    @Transactional
    public StudyResponse createStudy(StudyRequest request) {
        UUID currentOrg = TenantContext.getOrganizationId();
        
        if (currentOrg != null && request.getOrganizationId() != null && 
            !currentOrg.equals(request.getOrganizationId())) {
            throw new UnauthorizedException("No puede crear estudios en otra organización");
        }
        
        if (currentOrg != null && request.getOrganizationId() == null) {
            request.setOrganizationId(currentOrg);
        }
        
        Study study = studyMapper.toEntity(request);
        Study saved = studyRepository.save(study);
        
        log.info("📊 [PACS] Study created: {} for patient: {} in org: {}", 
                saved.getStudyInstanceUID(), saved.getPatientName(), saved.getOrganizationId());
        
        return studyMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudyResponse> findAllStudies() {
        UUID currentOrg = TenantContext.getOrganizationId();
        
        if (currentOrg != null) {
            return studyRepository.findByOrganizationId(currentOrg).stream()
                    .map(studyMapper::toResponse)
                    .collect(Collectors.toList());
        } else {
            return studyRepository.findAll().stream()
                    .map(studyMapper::toResponse)
                    .collect(Collectors.toList());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public StudyResponse findStudyById(UUID id) {
        UUID currentOrg = TenantContext.getOrganizationId();
        
        Study study = studyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Estudio no encontrado"));
        
        if (currentOrg != null && !currentOrg.equals(study.getOrganizationId())) {
            throw new UnauthorizedException("No tiene acceso a este estudio");
        }
        
        return studyMapper.toResponseWithSeries(study);
    }

    @Override
    @Transactional(readOnly = true)
    public StudyResponse findStudyByInstanceUID(String studyInstanceUID) {
        UUID currentOrg = TenantContext.getOrganizationId();
        
        Study study;
        if (currentOrg != null) {
            study = studyRepository.findByStudyInstanceUIDAndOrganizationId(studyInstanceUID, currentOrg)
                    .orElseThrow(() -> new NotFoundException("Estudio no encontrado"));
        } else {
            study = studyRepository.findByStudyInstanceUID(studyInstanceUID)
                    .orElseThrow(() -> new NotFoundException("Estudio no encontrado"));
        }
        
        return studyMapper.toResponseWithSeries(study);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudyResponse> searchStudies(String patientId, String patientName, 
                                           LocalDate studyDate, String modality, String accessionNumber) {
        UUID currentOrg = TenantContext.getOrganizationId();
        
        if (currentOrg == null) {
            throw new UnauthorizedException("Búsqueda requiere contexto de organización");
        }
        
        List<Study> studies = studyRepository.findStudiesWithFilters(
                patientId, patientName, studyDate, modality, accessionNumber, currentOrg);
        
        log.info("🔍 [PACS] Search found {} studies for org: {}", studies.size(), currentOrg);
        
        return studies.stream()
                .map(studyMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public StudyResponse updateStudy(UUID id, StudyRequest request) {
        UUID currentOrg = TenantContext.getOrganizationId();
        
        Study study = studyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Estudio no encontrado"));
        
        if (currentOrg != null && !currentOrg.equals(study.getOrganizationId())) {
            throw new UnauthorizedException("No puede modificar estudios de otra organización");
        }
        
        study.setStudyDescription(request.getStudyDescription());
        study.setReferringPhysician(request.getReferringPhysician());
        
        Study saved = studyRepository.save(study);
        log.info("📝 [PACS] Study updated: {}", saved.getStudyInstanceUID());
        
        return studyMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteStudy(UUID id) {
        UUID currentOrg = TenantContext.getOrganizationId();
        
        Study study = studyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Estudio no encontrado"));
        
        if (currentOrg != null && !currentOrg.equals(study.getOrganizationId())) {
            throw new UnauthorizedException("No puede eliminar estudios de otra organización");
        }
        
        studyRepository.deleteById(id);
        log.info("🗑️ [PACS] Study deleted: {}", study.getStudyInstanceUID());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeriesResponse> getSeriesByStudy(UUID studyId) {
        UUID currentOrg = TenantContext.getOrganizationId();
        
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new NotFoundException("Estudio no encontrado"));
        
        if (currentOrg != null && !currentOrg.equals(study.getOrganizationId())) {
            throw new UnauthorizedException("No tiene acceso a este estudio");
        }
        
        return seriesRepository.findByStudyId(studyId).stream()
                .map(seriesMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Long getStudyCount() {
        UUID currentOrg = TenantContext.getOrganizationId();
        
        if (currentOrg != null) {
            return studyRepository.countByOrganizationId(currentOrg);
        } else {
            return studyRepository.count();
        }
    }

    // ========== MÉTODOS FALTANTES IMPLEMENTADOS ==========

    @Override
    @Transactional(readOnly = true)
    public List<StudyResponse> findStudiesByPatientId(String patientId) {
        UUID currentOrg = TenantContext.getOrganizationId();
        
        if (currentOrg == null) {
            throw new UnauthorizedException("Búsqueda requiere contexto de organización");
        }
        
        List<Study> studies = studyRepository.findByPatientIdAndOrganizationId(patientId, currentOrg);
        
        log.info("🔍 [PACS] Found {} studies for patient: {} in org: {}", 
                studies.size(), patientId, currentOrg);
        
        return studies.stream()
                .map(studyMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public StudyResponse reprocessStudy(UUID id) {
        UUID currentOrg = TenantContext.getOrganizationId();
        
        Study study = studyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Estudio no encontrado"));
        
        if (currentOrg != null && !currentOrg.equals(study.getOrganizationId())) {
            throw new UnauthorizedException("No puede reprocesar estudios de otra organización");
        }
        
        // Cambiar estado a processing
        String previousStatus = study.getStatus();
        study.setStatus("PROCESSING");
        Study saved = studyRepository.save(study);
        
        try {
            // TODO: Aquí implementar la lógica real de reprocesamiento
            // Ejemplos:
            // - Revalidar metadatos DICOM
            // - Reenviar a DCM4CHEE
            // - Regenerar thumbnails
            // - Recalcular estadísticas
            
            // Por ahora simulamos el reprocesamiento exitoso
            Thread.sleep(1000); // Simular procesamiento
            
            study.setStatus("COMPLETED");
            saved = studyRepository.save(study);
            
            log.info("🔄 [PACS] Study reprocessing completed: {} (was: {})", 
                    saved.getStudyInstanceUID(), previousStatus);
            
        } catch (Exception e) {
            // En caso de error, revertir estado
            study.setStatus("ERROR");
            studyRepository.save(study);
            
            log.error("❌ [PACS] Study reprocessing failed: {} - {}", 
                     saved.getStudyInstanceUID(), e.getMessage());
            
            throw new RuntimeException("Error en reprocesamiento: " + e.getMessage());
        }
        
        return studyMapper.toResponse(saved);
    }

    // ========== MÉTODOS ADICIONALES IMPLEMENTADOS ==========

    @Override
    @Transactional(readOnly = true)
    public List<StudyResponse> findStudiesByDateRange(LocalDate startDate, LocalDate endDate) {
        UUID currentOrg = TenantContext.getOrganizationId();
        
        if (currentOrg == null) {
            throw new UnauthorizedException("Búsqueda requiere contexto de organización");
        }
        
        List<Study> studies = studyRepository.findByStudyDateBetweenAndOrganizationId(
                startDate, endDate, currentOrg);
        
        return studies.stream()
                .map(studyMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudyResponse> findStudiesByModality(String modality) {
        UUID currentOrg = TenantContext.getOrganizationId();
        
        if (currentOrg == null) {
            throw new UnauthorizedException("Búsqueda requiere contexto de organización");
        }
        
        List<Study> studies = studyRepository.findByModalityAndOrganizationId(modality, currentOrg);
        
        return studies.stream()
                .map(studyMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudyResponse> findStudiesByReferringPhysician(String physician) {
        UUID currentOrg = TenantContext.getOrganizationId();
        
        if (currentOrg == null) {
            throw new UnauthorizedException("Búsqueda requiere contexto de organización");
        }
        
        // Buscar estudios que contengan el nombre del médico
        List<Study> studies = studyRepository.findStudiesWithFilters(
                null, null, null, null, null, currentOrg)
                .stream()
                .filter(study -> study.getReferringPhysician() != null && 
                               study.getReferringPhysician().toLowerCase().contains(physician.toLowerCase()))
                .collect(Collectors.toList());
        
        return studies.stream()
                .map(studyMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public StudyStatsResponse getDetailedStudyStats() {
        UUID currentOrg = TenantContext.getOrganizationId();
        
        if (currentOrg == null) {
            throw new UnauthorizedException("Estadísticas requieren contexto de organización");
        }
        
        Long totalStudies = studyRepository.countByOrganizationId(currentOrg);
        List<Object[]> modalityStats = studyRepository.countByModalityAndOrganizationId(currentOrg);
        
        // Convertir estadísticas de modalidad
        List<ModalityStatsResponse> modalityStatsList = modalityStats.stream()
                .map(stat -> ModalityStatsResponse.builder()
                        .modality((String) stat[0])
                        .count((Long) stat[1])
                        .build())
                .collect(Collectors.toList());
        
        return StudyStatsResponse.builder()
                .totalStudies(totalStudies)
                .modalityStats(modalityStatsList)
                .organizationId(currentOrg)
                .timestamp(System.currentTimeMillis())
                .build();
    }
}
