package com.restaurante.backend.repository;

import com.restaurante.backend.domain.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    List<Customer> findByTenantId(Long tenantId);

    List<Customer> findByTenantIdAndNameContainingIgnoreCase(Long tenantId, String name);
}
