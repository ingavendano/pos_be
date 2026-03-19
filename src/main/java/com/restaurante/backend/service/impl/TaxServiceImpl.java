package com.restaurante.backend.service.impl;

import com.restaurante.backend.domain.entity.Tax;
import com.restaurante.backend.domain.entity.Tenant;
import com.restaurante.backend.repository.TaxRepository;
import com.restaurante.backend.repository.TenantRepository;
import com.restaurante.backend.security.TenantSecurityService;
import com.restaurante.backend.service.TaxService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaxServiceImpl implements TaxService {

    private final TaxRepository taxRepository;
    private final TenantRepository tenantRepository;
    private final TenantSecurityService tenantSecurityService;

    @Override
    @Transactional
    public Tax createTax(Long tenantId, Tax tax) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new com.restaurante.backend.exception.ResourceNotFoundException("Tenant not found"));
        tax.setTenant(tenant);
        return taxRepository.save(tax);
    }

    @Override
    @Transactional
    public Tax updateTax(Long id, Tax taxDetails) {
        Long tenantId = tenantSecurityService.getCurrentTenantId();
        Tax tax = taxRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new com.restaurante.backend.exception.ResourceNotFoundException("Tax not found"));

        tax.setName(taxDetails.getName());
        tax.setPercentage(taxDetails.getPercentage());
        tax.setIsActive(taxDetails.getIsActive());

        return taxRepository.save(tax);
    }

    @Override
    public Optional<Tax> getTaxById(Long id) {
        Long tenantId = tenantSecurityService.getCurrentTenantId();
        return taxRepository.findByIdAndTenantId(id, tenantId);
    }

    @Override
    public List<Tax> getTaxesByTenantId(Long tenantId) {
        return taxRepository.findByTenantId(tenantId);
    }

    @Override
    public List<Tax> getActiveTaxesByTenantId(Long tenantId) {
        return taxRepository.findByTenantIdAndIsActiveTrue(tenantId);
    }

    @Override
    @Transactional
    public void deleteTax(Long id) {
        Long tenantId = tenantSecurityService.getCurrentTenantId();
        taxRepository.deleteByIdAndTenantId(id, tenantId);
    }
}
