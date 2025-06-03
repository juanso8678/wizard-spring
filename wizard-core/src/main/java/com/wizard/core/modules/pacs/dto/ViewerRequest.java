package com.wizard.core.modules.pacs.dto;

import lombok.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViewerRequest {
    private String studyInstanceUID;
    private String seriesInstanceUID;
    private UUID organizationId;
    private String viewerType; // WEB, DESKTOP, MOBILE
    private Boolean fullscreen;
}