package com.restaurante.backend.service;

import com.restaurante.backend.domain.entity.Tenant;

import java.util.List;
import java.util.Optional;

public interface TenantService {
    Tenant createTenant(Tenant tenant);

    Tenant updateTenant(Long id, Tenant tenantDetails);

    Optional<Tenant> getTenantById(Long id);

    Optional<Tenant> getTenantByDomain(String domain);

    List<Tenant> getAllTenants();

    void deleteTenant(Long id);
}
