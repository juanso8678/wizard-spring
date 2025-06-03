package com.wizard.core.modules.pacs.mapper;

import com.wizard.core.modules.pacs.dto.DicomNodeRequest;
import com.wizard.core.modules.pacs.dto.DicomNodeResponse;
import com.wizard.core.modules.pacs.entity.DicomNode;
import org.springframework.stereotype.Component;

@Component
public class DicomNodeMapper {

    public DicomNode toEntity(DicomNodeRequest request) {
        return DicomNode.builder()
                .aeTitle(request.getAeTitle())
                .hostname(request.getHostname())
                .port(request.getPort())
                .description(request.getDescription())
                .nodeType(request.getNodeType())
                .organizationId(request.getOrganizationId())
                .queryRetrieve(request.getQueryRetrieve())
                .store(request.getStore())
                .echo(request.getEcho())
                .find(request.getFind())
                .move(request.getMove())
                .get(request.getGet())
                .active(true)
                .build();
    }

    public DicomNodeResponse toResponse(DicomNode node) {
        return DicomNodeResponse.builder()
                .id(node.getId())
                .aeTitle(node.getAeTitle())
                .hostname(node.getHostname())
                .port(node.getPort())
                .description(node.getDescription())
                .nodeType(node.getNodeType())
                .organizationId(node.getOrganizationId())
                .queryRetrieve(node.getQueryRetrieve())
                .store(node.getStore())
                .echo(node.getEcho())
                .find(node.getFind())
                .move(node.getMove())
                .get(node.getGet())
                .active(node.getActive())
                .createdAt(node.getCreatedAt())
                .build();
    }
}