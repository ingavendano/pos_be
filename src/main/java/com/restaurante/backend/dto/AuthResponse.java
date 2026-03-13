package com.restaurante.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private String refreshToken;
    @Builder.Default
    private String type = "Bearer";
    private Long id;
    private String username;
    private String name;
    private String role;
    private Long tenantId;
    private Long branchId;
    private String branchName;
    private List<PermissionDto> permissions;
}
