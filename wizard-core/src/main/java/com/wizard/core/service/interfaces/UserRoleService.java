package com.wizard.core.service.interfaces;

import com.wizard.core.dto.*;
import java.util.List;
import java.util.UUID;

public interface UserRoleService {
    UserRoleResponse assignRole(UserRoleRequest request);
    List<UserRoleResponse> findByUser(UUID userId);
    List<UserRoleResponse> findByRole(UUID roleId);
    void removeRole(UUID userId, UUID roleId);
    List<String> getUserPermissions(UUID userId);
}