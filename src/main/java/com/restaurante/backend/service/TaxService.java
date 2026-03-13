package com.restaurante.backend.service;

import com.restaurante.backend.domain.entity.Tax;

import java.util.List;
import java.util.Optional;

public interface TaxService {
    Tax createTax(Long tenantId, Tax tax);

    Tax updateTax(Long id, Tax taxDetails);

    Optional<Tax> getTaxById(Long id);

    List<Tax> getTaxesByTenantId(Long tenantId);

    List<Tax> getActiveTaxesByTenantId(Long tenantId);

    void deleteTax(Long id);
}
