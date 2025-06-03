
package com.wizard.core.dto;

import lombok.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSedeRequest {
    private UUID userId;
    private UUID sedeId;
}