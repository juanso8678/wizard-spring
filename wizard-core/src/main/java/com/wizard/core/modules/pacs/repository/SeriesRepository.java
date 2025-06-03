package com.wizard.core.modules.pacs.repository;

import com.wizard.core.modules.pacs.entity.Series;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SeriesRepository extends JpaRepository<Series, UUID> {
    
    List<Series> findByStudyId(UUID studyId);
    
    Optional<Series> findBySeriesInstanceUID(String seriesInstanceUID);
    
    List<Series> findByStudyIdAndModality(UUID studyId, String modality);
    
    @Query("SELECT s FROM Series s WHERE s.study.organizationId = :organizationId")
    List<Series> findByOrganizationId(@Param("organizationId") UUID organizationId);
    
    @Query("SELECT COUNT(s) FROM Series s WHERE s.study.id = :studyId")
    Integer countByStudyId(@Param("studyId") UUID studyId);
}