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
    private String currencySymbol;
    private String currency;
    // can add logoUrl here if needed later
}
