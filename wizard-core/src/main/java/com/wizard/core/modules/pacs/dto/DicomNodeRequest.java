package com.wizard.core.modules.pacs.dto;

import lombok.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DicomNodeRequest {
    private String aeTitle;
    private String hostname;
    private Integer port;
    private String description;
    private String nodeType; // SCU, SCP, BOTH
    private UUID organizationId;
    private Boolean queryRetrieve;
    private Boolean store;
    private Boolean echo;
    private Boolean find;
    private Boolean move;
    private Boolean get;
}