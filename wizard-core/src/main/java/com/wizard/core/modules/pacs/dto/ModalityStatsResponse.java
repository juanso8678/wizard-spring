package com.wizard.core.modules.pacs.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModalityStatsResponse {
    private String modality;
    private Long count;
}