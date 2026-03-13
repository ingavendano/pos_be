package com.restaurante.backend.dto;

import lombok.Data;
import lombok.Builder;

import java.util.List;

@Data
@Builder
public class RoleResponse {
    private Long id;
    private String name;
    private String description;
    private List<PermissionResponse> permissions;

    @Data
    @Builder
    public static class PermissionResponse {
        private Long id;
        private String component;
        private Boolean canRead;
        private Boolean canWrite;
        private Boolean canDelete;
    }
}
