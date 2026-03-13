package com.restaurante.backend.dto;

import com.restaurante.backend.domain.entity.User;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponseDto {
    private Long id;
    private String username;
    private String name;
    private Long roleId;
    private String roleName;
    private Long branchId;
    private Long tenantId;
    private Boolean isActive;

    public static UserResponseDto fromEntity(User user) {
        if (user == null)
            return null;
        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .roleId(user.getRole() != null ? user.getRole().getId() : null)
                .roleName(user.getRole() != null ? user.getRole().getName() : null)
                .branchId(user.getBranch() != null ? user.getBranch().getId() : null)
                .tenantId(user.getTenant() != null ? user.getTenant().getId() : null)
                .isActive(user.getIsActive())
                .build();
    }
}
