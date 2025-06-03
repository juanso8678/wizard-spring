package com.wizard.core.service.interfaces;

import com.wizard.core.dto.OrganizationRequest;
import com.wizard.core.dto.OrganizationResponse;

import java.util.List;
import java.util.UUID;

public interface OrganizationService {
    OrganizationResponse create(OrganizationRequest request);
    List<OrganizationResponse> findAll();
    OrganizationResponse findById(UUID id);
    OrganizationResponse update(UUID id, OrganizationRequest request);
    void delete(UUID id);
}