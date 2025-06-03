package com.wizard.core.service.impl;

import com.wizard.core.config.TenantContext;
import com.wizard.core.dto.OrganizationRequest;
import com.wizard.core.dto.OrganizationResponse;
import com.wizard.core.entity.Organization;
import com.wizard.core.exception.NotFoundException;
import com.wizard.core.exception.UnauthorizedException;
import com.wizard.core.mapper.OrganizationMapper;
import com.wizard.core.repository.OrganizationRepository;
import com.wizard.core.service.interfaces.OrganizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository organizationRepository;

    @Override
    public OrganizationResponse create(OrganizationRequest request) {
        UUID currentOrg = TenantContext.getOrganizationId();
        
        // Solo super admins (sin organización) pueden crear organizaciones
        if (currentOrg != null) {
            throw new UnauthorizedException("Solo super administradores pueden crear organizaciones");
        }
        
        Organization organization = OrganizationMapper.toEntity(request);
        Organization saved = organizationRepository.save(organization);
        
        log.info("Organization created: {}", saved.getName());
        return OrganizationMapper.toResponse(saved);
    }

    @Override
    public List<OrganizationResponse> findAll() {
        UUID currentOrg = TenantContext.getOrganizationId();
        
        if (currentOrg != null) {
            // Solo puede ver su propia organización
            return organizationRepository.findById(currentOrg)
                    .map(org -> List.of(OrganizationMapper.toResponse(org)))
                    .orElse(List.of());
        } else {
            // Super admin - ve todas las organizaciones
            return organizationRepository.findAll().stream()
                    .map(OrganizationMapper::toResponse)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public OrganizationResponse findById(UUID id) {
        UUID currentOrg = TenantContext.getOrganizationId();
        
        // Validar acceso por organización
        if (currentOrg != null && !currentOrg.equals(id)) {
            throw new UnauthorizedException("No tiene acceso a esta organización");
        }
        
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Organización no encontrada"));
        
        return OrganizationMapper.toResponse(organization);
    }

    @Override
    public OrganizationResponse update(UUID id, OrganizationRequest request) {
        UUID currentOrg = TenantContext.getOrganizationId();
        
        // Validar acceso por organización
        if (currentOrg != null && !currentOrg.equals(id)) {
            throw new UnauthorizedException("No puede modificar otra organización");
        }
        
        Organization existing = organizationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Organización no encontrada"));

        existing.setName(request.getName());
        existing.setAddress(request.getAddress());
        existing.setContactEmail(request.getContactEmail());
        existing.setContactPhone(request.getContactPhone());
        existing.setDescription(request.getDescription());
        existing.setLogo(request.getLogo());

        Organization saved = organizationRepository.save(existing);
        log.info("Organization updated: {}", saved.getName());
        
        return OrganizationMapper.toResponse(saved);
    }

    @Override
    public void delete(UUID id) {
        UUID currentOrg = TenantContext.getOrganizationId();
        
        // Solo super admins pueden eliminar organizaciones
        if (currentOrg != null) {
            throw new UnauthorizedException("Solo super administradores pueden eliminar organizaciones");
        }
        
        if (!organizationRepository.existsById(id)) {
            throw new NotFoundException("Organización no encontrada para eliminar");
        }
        
        organizationRepository.deleteById(id);
        log.info("Organization deleted: {}", id);
    }
}