package com.wizard.core.modules.pacs.service.impl;

import com.wizard.core.config.TenantContext;
import com.wizard.core.modules.pacs.config.PacsConfig;
import com.wizard.core.modules.pacs.dto.ViewerRequest;
import com.wizard.core.modules.pacs.dto.ViewerResponse;
import com.wizard.core.modules.pacs.service.interfaces.ViewerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ViewerServiceImpl implements ViewerService {

    private final PacsConfig pacsConfig;  // ✅ Usar configuración

    @Override
    public ViewerResponse generateViewerUrl(ViewerRequest request) {
        UUID currentOrg = TenantContext.getOrganizationId();
        
        String sessionToken = generateSecureToken(request.getStudyInstanceUID(), currentOrg);
        
        String viewerUrl = String.format(
                "%s/wizard-viewer?study=%s&org=%s&token=%s&type=%s&fullscreen=%s",
                pacsConfig.getViewer().getBaseUrl(),  // ✅ Configuración
                request.getStudyInstanceUID(),
                currentOrg,
                sessionToken,
                request.getViewerType(),
                request.getFullscreen()
        );
        
        log.info("🖥️ [VIEWER] Generated viewer URL for study: {} org: {}", 
                request.getStudyInstanceUID(), currentOrg);
        
        return ViewerResponse.builder()
                .viewerUrl(viewerUrl)
                .studyInstanceUID(request.getStudyInstanceUID())
                .viewerType(request.getViewerType())
                .sessionToken(sessionToken)
                .fullscreen(request.getFullscreen())
                .timeoutMinutes(pacsConfig.getViewer().getSessionTimeoutMinutes())  // ✅ Configuración
                .build();
    }

    @Override
    public ViewerResponse generateViewerUrlForStudy(String studyInstanceUID) {
        ViewerRequest request = ViewerRequest.builder()
                .studyInstanceUID(studyInstanceUID)
                .viewerType("WEB")
                .fullscreen(false)
                .build();
        
        return generateViewerUrl(request);
    }

    @Override
    public boolean validateViewerAccess(String sessionToken) {
        return sessionToken != null && sessionToken.startsWith("wizard_viewer_");
    }

    private String generateSecureToken(String studyInstanceUID, UUID organizationId) {
        return "wizard_viewer_" + organizationId + "_" + 
               studyInstanceUID.hashCode() + "_" + 
               System.currentTimeMillis();
    }
}