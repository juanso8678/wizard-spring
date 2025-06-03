
package com.wizard.core.service.interfaces;

import com.wizard.core.dto.UserSedeRequest;
import com.wizard.core.dto.UserSedeResponse;
import java.util.List;
import java.util.UUID;

public interface UserSedeService {
    UserSedeResponse assignUserToSede(UserSedeRequest request);
    List<UserSedeResponse> findAll();
    List<UserSedeResponse> findByUserId(UUID userId);
    List<UserSedeResponse> findBySedeId(UUID sedeId);
    void removeUserFromSede(UUID userId, UUID sedeId);
    boolean isUserAssignedToSede(UUID userId, UUID sedeId);
}
