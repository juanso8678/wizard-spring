package com.wizard.core.modules.pacs.service.interfaces;

import com.wizard.core.modules.pacs.dto.ViewerRequest;
import com.wizard.core.modules.pacs.dto.ViewerResponse;

public interface ViewerService {
    ViewerResponse generateViewerUrl(ViewerRequest request);
    ViewerResponse generateViewerUrlForStudy(String studyInstanceUID);
    boolean validateViewerAccess(String sessionToken);
}