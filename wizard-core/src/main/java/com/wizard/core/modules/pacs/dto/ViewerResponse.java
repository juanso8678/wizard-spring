package com.wizard.core.modules.pacs.dto;

import lombok.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViewerResponse {
    private String viewerUrl;
    private String studyInstanceUID;
    private String viewerType;
    private String sessionToken;
    private Boolean fullscreen;
    private Integer timeoutMinutes;
}