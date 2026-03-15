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
    private String nrc;
    private String giro;
    private String documentType;
    private String documentNumber;
    private String departamento;
    private String municipio;
    private String complemento;
}
