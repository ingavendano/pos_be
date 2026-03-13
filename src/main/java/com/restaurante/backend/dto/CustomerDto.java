package com.restaurante.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerDto {
    private Long id;
    private String name;
    private String nit;
    private String email;
    private String phone;
}
