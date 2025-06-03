package com.wizard.core.modules.pacs.repository;

import com.wizard.core.modules.pacs.entity.DicomNode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DicomNodeRepository extends JpaRepository<DicomNode, UUID> {
    
    List<DicomNode> findByOrganizationId(UUID organizationId);
    
    List<DicomNode> findByOrganizationIdAndActiveTrue(UUID organizationId);
    
    Optional<DicomNode> findByAeTitleAndOrganizationId(String aeTitle, UUID organizationId);
    
    List<DicomNode> findByNodeTypeAndOrganizationId(String nodeType, UUID organizationId);
    
    boolean existsByAeTitleAndOrganizationId(String aeTitle, UUID organizationId);
}