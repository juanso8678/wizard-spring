package com.wizard.core.modules.pacs.dto;

import lombok.*;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyStatsResponse {
    private Long totalStudies;
    private List<ModalityStatsResponse> modalityStats;
    private UUID organizationId;
    private Long timestamp;
}