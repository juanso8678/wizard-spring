package com.wizard.core.modules.pacs.repository;

import com.wizard.core.modules.pacs.entity.Instance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InstanceRepository extends JpaRepository<Instance, UUID> {
    
    List<Instance> findBySeriesId(UUID seriesId);
    
    Optional<Instance> findBySopInstanceUID(String sopInstanceUID);
    
    @Query("SELECT i FROM Instance i WHERE i.series.study.organizationId = :organizationId")
    List<Instance> findByOrganizationId(@Param("organizationId") UUID organizationId);
    
    @Query("SELECT COUNT(i) FROM Instance i WHERE i.series.id = :seriesId")
    Integer countBySeriesId(@Param("seriesId") UUID seriesId);
    
    @Query("SELECT COUNT(i) FROM Instance i WHERE i.series.study.id = :studyId")
    Integer countByStudyId(@Param("studyId") UUID studyId);
}