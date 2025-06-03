package com.wizard.core.modules.pacs.service.interfaces;

import com.wizard.core.modules.pacs.dto.DicomNodeRequest;
import com.wizard.core.modules.pacs.dto.DicomNodeResponse;
import java.util.List;
import java.util.Map;  // ✅ IMPORT AGREGADO
import java.util.UUID;

public interface DicomNodeService {
    DicomNodeResponse createDicomNode(DicomNodeRequest request);
    List<DicomNodeResponse> findAllDicomNodes();
    DicomNodeResponse findDicomNodeById(UUID id);
    DicomNodeResponse updateDicomNode(UUID id, DicomNodeRequest request);
    void deleteDicomNode(UUID id);
    boolean performEcho(UUID nodeId);
    List<DicomNodeResponse> findActiveNodes();
    DicomNodeResponse toggleActiveStatus(UUID nodeId);
    Map<String, Boolean> performBatchEcho();  // ✅ Map ya importado
}