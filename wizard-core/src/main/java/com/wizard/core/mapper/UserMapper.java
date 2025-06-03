package com.wizard.core.mapper;

import com.wizard.core.dto.UserRequest;
import com.wizard.core.dto.UserResponse;
import com.wizard.core.entity.User;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    public User toEntity(UserRequest request) {
        return User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword())) // ✅ Encriptar
                .name(request.getName())
                .role(request.getRole())
                .activo(request.isActivo())
                .organizationId(request.getOrganizationId())
                .build();
    }

    public UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .name(user.getName())
                .role(user.getRole())
                .activo(user.isActivo())
                .organizationId(user.getOrganizationId()) 
                .build();
    }
}