package com.wizard.core.modules.pacs.mapper;

import com.wizard.core.modules.pacs.dto.SeriesResponse;
import com.wizard.core.modules.pacs.entity.Series;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SeriesMapper {

    private final InstanceMapper instanceMapper;

    public SeriesResponse toResponse(Series series) {
        return SeriesResponse.builder()
                .id(series.getId())
                .seriesInstanceUID(series.getSeriesInstanceUID())
                .seriesNumber(series.getSeriesNumber())
                .seriesDescription(series.getSeriesDescription())
                .modality(series.getModality())
                .seriesTime(series.getSeriesTime())
                .bodyPartExamined(series.getBodyPartExamined())
                .protocolName(series.getProtocolName())
                .manufacturer(series.getManufacturer())
                .numberOfInstances(series.getNumberOfInstances())
                .status(series.getStatus())
                .createdAt(series.getCreatedAt())
                .build();
    }

    public SeriesResponse toResponseWithInstances(Series series) {
        SeriesResponse response = toResponse(series);
        
        if (series.getInstances() != null) {
            List<com.wizard.core.modules.pacs.dto.InstanceResponse> instanceResponses = 
                series.getInstances().stream()
                    .map(instanceMapper::toResponse)
                    .collect(Collectors.toList());
            response.setInstances(instanceResponses);
        }
        
        return response;
    }
}