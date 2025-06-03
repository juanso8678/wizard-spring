package com.wizard.core.service.interfaces;

import com.wizard.core.dto.*;
import java.util.List;
import java.util.UUID;

public interface RoleService {
    RoleResponse create(RoleRequest request);
    List<RoleResponse> findAll();
    RoleResponse findById(UUID id);
    RoleResponse update(UUID id, RoleRequest request);
    void delete(UUID id);
    List<RoleResponse> findByOrganization(UUID organizationId);
    RoleResponse assignPermissions(UUID roleId, List<UUID> permissionIds);
}