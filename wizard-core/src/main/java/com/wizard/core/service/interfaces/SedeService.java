package com.wizard.core.service.interfaces;

import com.wizard.core.dto.SedeRequest;
import com.wizard.core.dto.SedeResponse;

import java.util.List;
import java.util.UUID;

public interface SedeService {
    SedeResponse create(SedeRequest request);
    List<SedeResponse> findAll();
    SedeResponse findById(UUID id);
    SedeResponse update(UUID id, SedeRequest request);
    void delete(UUID id);
}
