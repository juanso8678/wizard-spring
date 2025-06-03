package com.wizard.core.service.impl;

import com.wizard.core.config.TenantContext;
import com.wizard.core.dto.SedeRequest;
import com.wizard.core.dto.SedeResponse;
import com.wizard.core.entity.Organization;
import com.wizard.core.entity.Sede;
import com.wizard.core.exception.NotFoundException;
import com.wizard.core.exception.UnauthorizedException;
import com.wizard.core.mapper.SedeMapper;
import com.wizard.core.repository.OrganizationRepository;
import com.wizard.core.repository.SedeRepository;
import com.wizard.core.service.interfaces.SedeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SedeServiceImpl implements SedeService {

    private final SedeRepository sedeRepository;
    private final OrganizationRepository organizationRepository;
    private final SedeMapper sedeMapper;

    @Override
    public SedeResponse create(SedeRequest request) {
        UUID currentOrg = TenantContext.getOrganizationId();
        
        // Si hay contexto de organización, debe coincidir con la request
        if (currentOrg != null && !currentOrg.equals(request.getOrganizationId())) {
            throw new UnauthorizedException("No puede crear sedes en otra organización");
        }
        
        Organization org = organizationRepository.findById(request.getOrganizationId())
                .orElseThrow(() -> new NotFoundException("Organización no encontrada"));

        Sede sede = sedeMapper.toEntity(request, org);
        Sede saved = sedeRepository.save(sede);
        
        log.info("Sede created: {} for organization: {}", saved.getName(), org.getName());
        return sedeMapper.toResponse(saved);
    }

    @Override
    public List<SedeResponse> findAll() {
        UUID currentOrg = TenantContext.getOrganizationId();
        
        if (currentOrg != null) {
            // Solo sedes de la organización actual
            Organization org = organizationRepository.findById(currentOrg)
                    .orElseThrow(() -> new NotFoundException("Organización no encontrada"));
            
            return sedeRepository.findByOrganization(org).stream()
                    .map(sedeMapper::toResponse)
                    .collect(Collectors.toList());
        } else {
            // Super admin - ve todas las sedes
            return sedeRepository.findAll().stream()
                    .map(sedeMapper::toResponse)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public SedeResponse findById(UUID id) {
        UUID currentOrg = TenantContext.getOrganizationId();
        
        Sede sede = sedeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sede no encontrada"));
        
        // Validar acceso por organización
        if (currentOrg != null && !currentOrg.equals(sede.getOrganization().getId())) {
            throw new UnauthorizedException("No tiene acceso a esta sede");
        }
        
        return sedeMapper.toResponse(sede);
    }

    @Override
    public SedeResponse update(UUID id, SedeRequest request) {
        UUID currentOrg = TenantContext.getOrganizationId();
        
        Sede sede = sedeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sede no encontrada"));
        
        // Validar acceso por organización
        if (currentOrg != null && !currentOrg.equals(sede.getOrganization().getId())) {
            throw new UnauthorizedException("No puede modificar sedes de otra organización");
        }
        
        // Validar que la nueva organización coincida con el contexto
        if (currentOrg != null && !currentOrg.equals(request.getOrganizationId())) {
            throw new UnauthorizedException("No puede transferir sedes a otra organización");
        }

        Organization org = organizationRepository.findById(request.getOrganizationId())
                .orElseThrow(() -> new NotFoundException("Organización no encontrada"));

        sede.setName(request.getName());
        sede.setAddress(request.getAddress());
        sede.setContact(request.getContact());
        sede.setEmail(request.getEmail());
        sede.setPhone(request.getPhone());
        sede.setTypeSede(request.getTypeSede());
        sede.setDescription(request.getDescription());
        sede.setOrganization(org);

        Sede saved = sedeRepository.save(sede);
        log.info("Sede updated: {} for organization: {}", saved.getName(), org.getName());
        
        return sedeMapper.toResponse(saved);
    }

    @Override
    public void delete(UUID id) {
        UUID currentOrg = TenantContext.getOrganizationId();
        
        Sede sede = sedeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sede no encontrada"));
        
        // Validar acceso por organización
        if (currentOrg != null && !currentOrg.equals(sede.getOrganization().getId())) {
            throw new UnauthorizedException("No puede eliminar sedes de otra organización");
        }
        
        sedeRepository.deleteById(id);
        log.info("Sede deleted: {} from organization: {}", sede.getName(), sede.getOrganization().getName());
    }
}