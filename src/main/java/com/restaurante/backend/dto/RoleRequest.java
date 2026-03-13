package com.restaurante.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.List;

@Data
public class RoleRequest {

    @NotBlank(message = "El nombre del rol es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre del rol debe tener entre 2 y 100 caracteres")
    private String name;

    @Size(max = 255, message = "La descripción no puede superar 255 caracteres")
    private String description;

    @NotEmpty(message = "Debe definir al menos un permiso")
    @Valid
    private List<PermissionRequest> permissions;

    @Data
    public static class PermissionRequest {
        @NotBlank(message = "El componente del permiso es obligatorio")
        private String component;
        @NotNull(message = "canRead es obligatorio")
        private Boolean canRead;
        @NotNull(message = "canWrite es obligatorio")
        private Boolean canWrite;
        @NotNull(message = "canDelete es obligatorio")
        private Boolean canDelete;
    }
}
