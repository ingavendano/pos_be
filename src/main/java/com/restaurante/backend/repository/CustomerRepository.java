package com.restaurante.backend.repository;

import com.restaurante.backend.domain.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    List<Customer> findByTenantId(Long tenantId);

    List<Customer> findByTenantIdAndNameContainingIgnoreCase(Long tenantId, String name);

    Optional<Customer> findByIdAndTenantId(Long id, Long tenantId);

    void deleteByIdAndTenantId(Long id, Long tenantId);
}
