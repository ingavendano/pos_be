package com.restaurante.backend.service.impl;

import com.restaurante.backend.domain.entity.Customer;
import com.restaurante.backend.domain.entity.Tenant;
import com.restaurante.backend.repository.CustomerRepository;
import com.restaurante.backend.repository.TenantRepository;
import com.restaurante.backend.security.TenantSecurityService;
import com.restaurante.backend.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final TenantRepository tenantRepository;
    private final TenantSecurityService tenantSecurityService;

    @Override
    public List<Customer> getAll(Long tenantId) {
        return customerRepository.findByTenantId(tenantId);
    }

    @Override
    public List<Customer> search(Long tenantId, String name) {
        return customerRepository.findByTenantIdAndNameContainingIgnoreCase(tenantId, name);
    }

    @Override
    public Optional<Customer> getById(Long id) {
        Long tenantId = tenantSecurityService.getCurrentTenantId();
        return customerRepository.findByIdAndTenantId(id, tenantId);
    }

    @Override
    @Transactional
    public Customer create(Long tenantId, Customer customer) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new com.restaurante.backend.exception.ResourceNotFoundException("Tenant not found"));
        customer.setTenant(tenant);
        return customerRepository.save(customer);
    }

    @Override
    @Transactional
    public Customer update(Long id, Customer updated) {
        Long tenantId = tenantSecurityService.getCurrentTenantId();
        Customer existing = customerRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(
                        () -> new com.restaurante.backend.exception.ResourceNotFoundException("Customer not found"));
        existing.setName(updated.getName());
        existing.setPhone(updated.getPhone());
        existing.setEmail(updated.getEmail());
        existing.setNit(updated.getNit());
        return customerRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Long tenantId = tenantSecurityService.getCurrentTenantId();
        customerRepository.deleteByIdAndTenantId(id, tenantId);
    }
}
