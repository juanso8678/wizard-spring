package com.wizard.core.mapper;

import com.wizard.core.dto.UserSedeRequest;
import com.wizard.core.dto.UserSedeResponse;
import com.wizard.core.entity.User;
import com.wizard.core.entity.Sede;
import com.wizard.core.entity.UserSede;
import org.springframework.stereotype.Component;

@Component
public class UserSedeMapper {

    public UserSede toEntity(UserSedeRequest request, User user, Sede sede) {
        return UserSede.builder()
                .user(user)
                .sede(sede)
                .build();
    }

    public UserSedeResponse toResponse(UserSede userSede) {
        return UserSedeResponse.builder()
                .id(userSede.getId())
                .userId(userSede.getUser().getId())
                .username(userSede.getUser().getUsername())
                .userEmail(userSede.getUser().getEmail())
                .userName(userSede.getUser().getName())
                .sedeId(userSede.getSede().getId())
                .sedeName(userSede.getSede().getName())
                .sedeAddress(userSede.getSede().getAddress())
                .organizationId(userSede.getSede().getOrganization().getId())
                .organizationName(userSede.getSede().getOrganization().getName())
                .build();
    }
}