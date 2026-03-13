package com.restaurante.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateOrderStatusDto {
    @NotBlank(message = "El estado es requerido")
    private String status;

    @Builder.Default
    private String paymentMethod = "CASH";
}
