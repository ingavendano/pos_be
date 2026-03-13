package com.restaurante.backend.service;

import com.restaurante.backend.domain.entity.Customer;
import java.util.List;
import java.util.Optional;

public interface CustomerService {
    List<Customer> getAll(Long tenantId);

    List<Customer> search(Long tenantId, String name);

    Optional<Customer> getById(Long id);

    Customer create(Long tenantId, Customer customer);

    Customer update(Long id, Customer customer);

    void delete(Long id);
}
