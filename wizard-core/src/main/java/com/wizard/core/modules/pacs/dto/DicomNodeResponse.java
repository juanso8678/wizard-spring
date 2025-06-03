package com.wizard.core.modules.pacs.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DicomNodeResponse {
    private UUID id;
    private String aeTitle;
    private String hostname;
    private Integer port;
    private String description;
    private String nodeType;
    private UUID organizationId;
    private Boolean queryRetrieve;
    private Boolean store;
    private Boolean echo;
    private Boolean find;
    private Boolean move;
    private Boolean get;
    private Boolean active;
    private LocalDateTime createdAt;
}