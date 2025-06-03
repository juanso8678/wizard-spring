package com.wizard.core.service.interfaces;

import com.wizard.core.dto.UserRequest;
import com.wizard.core.dto.UserResponse;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponse create(UserRequest request);
    UserResponse getById(UUID id);
    List<UserResponse> getAll();
    void delete(UUID id);
}
