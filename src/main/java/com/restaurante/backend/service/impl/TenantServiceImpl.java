package com.restaurante.backend.service.impl;

import com.restaurante.backend.domain.entity.Tenant;
import com.restaurante.backend.repository.TenantRepository;
import com.restaurante.backend.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;

    @Override
    @Transactional
    public Tenant createTenant(Tenant tenant) {
        return tenantRepository.save(tenant);
    }

    @Override
    @Transactional
    public Tenant updateTenant(Long id, Tenant tenantDetails) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new com.restaurante.backend.exception.ResourceNotFoundException("Tenant not found"));

        tenant.setName(tenantDetails.getName());
        tenant.setDomain(tenantDetails.getDomain());
        tenant.setCurrency(tenantDetails.getCurrency());
        tenant.setCurrencySymbol(tenantDetails.getCurrencySymbol());
        tenant.setNit(tenantDetails.getNit());
        tenant.setNrc(tenantDetails.getNrc());
        tenant.setGiro(tenantDetails.getGiro());
        tenant.setIsActive(tenantDetails.getIsActive());

        return tenantRepository.save(tenant);
    }

    @Override
    public Optional<Tenant> getTenantById(Long id) {
        return tenantRepository.findById(id);
    }

    @Override
    public Optional<Tenant> getTenantByDomain(String domain) {
        return tenantRepository.findByDomain(domain);
    }

    @Override
    public List<Tenant> getAllTenants() {
        return tenantRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteTenant(Long id) {
        tenantRepository.deleteById(id);
    }
}
