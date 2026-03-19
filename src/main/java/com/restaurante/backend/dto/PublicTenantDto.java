package com.restaurante.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PublicTenantDto {
    private String name;
    /** Dominio/subdominio del tenant (ej. ventaloca.tupos.app) para mostrar en login. */
    private String domain;
    private String currencySymbol;
    private String currency;
    /**
     * Tema visual: indigo | restaurant | retail | premium
     * El frontend lo aplica como clase CSS en el <body>.
     */
    private String theme;
}
