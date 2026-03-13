package com.restaurante.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PermissionDto {
    private String component;
    private Boolean canRead;
    private Boolean canWrite;
    private Boolean canDelete;
}
